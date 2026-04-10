package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;
import com.blog.service.BlogService;
import com.blog.service.MessageService;
import com.blog.service.ViewService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.blog.utils.XssCleanUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

/**
 * 博客服务实现类
 * 集成了Redis缓存策略，优化查询性能
 * 
 * @author Worlt
 * @date 2025/5/21
 */
@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogTagMapper blogTagMapper;
    @Autowired
    private BlogCustomTagMapper blogCustomTagMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ViewService viewService;
    @Autowired
    private BloomFilterServiceImpl bloomFilterService;

    // 消息服务，用于发送状态变更通知
    @Autowired
    private MessageService messageService;

    // 集成缓存服务
    @Autowired
    private BlogCacheServiceImpl blogCacheService;

    @Autowired
    private AdvancedCacheManagerImpl advancedCacheManager;

    /**
     * 分页查询博客文章
     * 
     * @param blogPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(BlogPageQueryDTO blogPageQueryDTO) {
        int pageNum = blogPageQueryDTO.getPage() != null ? blogPageQueryDTO.getPage() : 1;
        int pageSize = blogPageQueryDTO.getPageSize() != null ? blogPageQueryDTO.getPageSize() : 10;

        PageHelper.startPage(pageNum, pageSize);
        Page<BlogVO> page = blogMapper.page(blogPageQueryDTO);
        Long total = page.getTotal();
        List<BlogVO> blogVOList = page.getResult();

        if (!blogVOList.isEmpty()) {
            // 收集所有博客id
            List<Long> blogIds = blogVOList.stream().map(BlogVO::getId).collect(Collectors.toList());

            // 一次性获取所有博客的标签
            Map<Long, List<String>> blogTagMap = blogTagMapper.getTagsByBlogIds(blogIds);
            Map<Long, List<String>> blogCustomTagMap = blogCustomTagMapper.getCustomTagsByBlogIds(blogIds);
            log.info("博客标签映射：{}", blogTagMap);
            log.info("博客自定义标签映射：{}", blogCustomTagMap);

            // 组装数据
            // for (BlogVO blogVO : blogVOList) {
            // blogVO.setTagList(blogTagMap.getOrDefault(blogVO.getId(),
            // Collections.emptyList()));
            // blogVO.setCustomTagList(blogCustomTagMap.getOrDefault(blogVO.getId(),
            // Collections.emptyList()));
            // }

            // Map<String, List<String>> listMap = (Map<String, List<String>>)
            // blogTagMap.get(1L);
            // List<String> list = listMap.get("value");
            // System.out.println("list:"+ list);

            // 组装数据
            for (BlogVO blogVO : blogVOList) {
                // 处理标签
                Map<String, List<String>> tagListMap = (Map<String, List<String>>) blogTagMap.get(blogVO.getId());
                List<String> tagList = tagListMap != null ? tagListMap.get("value") : Collections.emptyList();

                // 处理自定义标签
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTagMap
                        .get(blogVO.getId());
                List<String> customTagList = customTagListMap != null ? customTagListMap.get("value")
                        : Collections.emptyList();

                // 调试打印信息
                // log.info("blog_id:{}", blogVO.getId());
                // log.info("tagListMap:{}", tagListMap);
                // log.info("customTagListMap:{}", customTagListMap);
                // log.info("tagList:{}", tagList);
                // log.info("customTagList:{}", customTagList);

                // 设置标签列表到blogVO对象
                blogVO.setTagList(tagList != null ? tagList : Collections.emptyList());
                blogVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());
            }

        }

        return new PageResult(total, blogVOList);
    }

    /**
     * 新增博客文章
     * 
     * @param blogAddDTO
     */
    @Override
    @Transactional
    public Long add(BlogAddDTO blogAddDTO) {
        // 应用XSS清理
        blogAddDTO.setTitle(XssCleanUtil.cleanAllTags(blogAddDTO.getTitle()));
        blogAddDTO.setContent(XssCleanUtil.cleanRichText(blogAddDTO.getContent()));

        // 检测到恶意脚本，清理后如果为空
        if (blogAddDTO.getTitle().isEmpty())
            throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogAddDTO.getContent().isEmpty())
            throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

        // 复制属性
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogAddDTO, blog);

        // if (userMapper.getById(blog.getUserId()) == null) throw new
        // RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);
        if (categoryMapper.getById(blog.getCategoryId()) == null)
            throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);

        // 设置默认值
        blog.setUserId(BaseContext.getCurrentId());
        blog.setStatus(BlogStatusConstant.PENDING);
        blog.setViews(0L);
        blog.setLikes(0L);
        blog.setDislikes(0L);
        blog.setCollections(0L);
        blog.setShares(0L);
        blog.setComments(0L);
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());

        // 设置置顶默认值，如果DTO中未指定
        if (blog.getIsTop() == null) {
            blog.setIsTop(0); // 默认不置顶
        }

        // 如果封面为空，可以设置默认封面或保持null
        // blog.setCover(blog.getCover() == null ? "默认封面URL" : blog.getCover());

        // 插入博客
        blogMapper.add(blog);
        // 获取插入博客的id
        Long id = blog.getId();

        // 同步将新博客 ID 加入布隆过滤器，确保新发布的博客能通过防护层检查
        bloomFilterService.add("blog:" + id);
        log.debug("新博客已同步至布隆过滤器: blog:{}", id);

        // 构造tag
        if (!blogAddDTO.getTagIdList().isEmpty()) {
            List<BlogTag> blogTags = new ArrayList<>();

            for (Long tagId : blogAddDTO.getTagIdList()) {
                BlogTag blogTag = BlogTag.builder()
                        .blogId(id)
                        .tagId(tagId)
                        .createTime(LocalDateTime.now())
                        .build();
                blogTags.add(blogTag);
            }

            if (!blogTags.isEmpty()) {
                blogTagMapper.insertBatch(blogTags);
            }

        }

        // 构造customTag
        if (!blogAddDTO.getCustomTagList().isEmpty()) {
            List<BlogCustomTag> blogCustomTags = new ArrayList<>();
            for (String customTag : blogAddDTO.getCustomTagList()) {
                BlogCustomTag blogCustomTag = BlogCustomTag.builder()
                        .blogId(id)
                        .customTag(customTag)
                        .createTime(LocalDateTime.now())
                        .build();
                blogCustomTags.add(blogCustomTag);
            }

            if (!blogCustomTags.isEmpty()) {
                blogCustomTagMapper.insertBatch(blogCustomTags);
            }
        }

        return id;
    }

    /**
     * 编辑博客
     * 
     * @param blogUpdateDTO
     */
    @Override
    @Transactional
    public void update(BlogUpdateDTO blogUpdateDTO) {
        // 清理XSS
        blogUpdateDTO.setTitle(XssCleanUtil.cleanAllTags(blogUpdateDTO.getTitle()));
        blogUpdateDTO.setContent(XssCleanUtil.cleanRichText(blogUpdateDTO.getContent()));

        // 清理结果
        if (blogUpdateDTO.getTitle().isEmpty())
            throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogUpdateDTO.getContent().isEmpty())
            throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

        // 查询原博客信息，用于检测状态变更
        Blog oldBlog = blogMapper.getById(blogUpdateDTO.getId());
        Integer oldStatus = oldBlog != null ? oldBlog.getStatus() : null;
        Integer newStatus = blogUpdateDTO.getStatus();
        Long authorId = oldBlog != null ? oldBlog.getUserId() : null;
        String blogTitle = blogUpdateDTO.getTitle();

        Blog blog = new Blog();
        BeanUtils.copyProperties(blogUpdateDTO, blog);

        // 删除所有标签
        blogTagMapper.deleteByBlogId(blog.getId());
        blogCustomTagMapper.deleteByBlogId(blog.getId());

        if (!blogUpdateDTO.getTagIdList().isEmpty()) {
            List<BlogTag> blogTags = new ArrayList<>();
            for (Long tagId : blogUpdateDTO.getTagIdList()) {
                BlogTag blogTag = BlogTag.builder()
                        .blogId(blog.getId())
                        .tagId(tagId)
                        .build();
                blogTags.add(blogTag);
            }
            blogTagMapper.insertBatch(blogTags);
        }

        if (!blogUpdateDTO.getCustomTagList().isEmpty()) {
            List<BlogCustomTag> blogCustomTags = new ArrayList<>();
            for (String customTag : blogUpdateDTO.getCustomTagList()) {
                BlogCustomTag blogCustomTag = BlogCustomTag.builder()
                        .blogId(blog.getId())
                        .customTag(customTag)
                        .build();
                blogCustomTags.add(blogCustomTag);
            }
            blogCustomTagMapper.insertBatch(blogCustomTags);
        }

        blog.setUpdateTime(LocalDateTime.now());
        blogMapper.update(blog);

        // 清除博客缓存，确保下次获取最新数据
        evictBlogCache(blog.getId());

        // 状态变更时发送系统通知给博客作者
        if (!Objects.equals(oldStatus, newStatus) && authorId != null && newStatus != null) {
            String statusMessage = blogUpdateDTO.getStatusMessage();
            sendStatusChangeNotification(blogTitle, newStatus, authorId, statusMessage);
        }
    }

    /**
     * 发送博客状态变更通知
     * 
     * @param blogTitle     博客标题
     * @param newStatus     新状态
     * @param authorId      作者ID
     * @param statusMessage 状态变更原因
     */
    private void sendStatusChangeNotification(String blogTitle, Integer newStatus, Long authorId,
            String statusMessage) {
        String message = buildStatusChangeMessage(blogTitle, newStatus, statusMessage);
        if (message != null) {
            try {
                messageService.sendSystemMessage(authorId, message);
                log.info("已发送状态变更通知给用户{}，博客标题：{}，新状态：{}", authorId, blogTitle, newStatus);
            } catch (Exception e) {
                log.error("发送状态变更通知失败，用户ID：{}，博客标题：{}", authorId, blogTitle, e);
            }
        }
    }

    /**
     * 构建状态变更消息内容
     * 
     * @param blogTitle     博客标题
     * @param status        新状态
     * @param statusMessage 状态变更原因
     * @return 消息内容，如果不需要通知则返回null
     */
    private String buildStatusChangeMessage(String blogTitle, Integer status, String statusMessage) {
        // 根据状态码构建不同的通知消息
        String baseMessage;
        switch (status) {
            case 1: // 已通过 (BlogStatusConstant.APPROVED)
                return String.format("您的博客《%s》审核已通过，现已发布。", blogTitle);
            case 2: // 已拒绝 (BlogStatusConstant.REJECTED)
                baseMessage = String.format("您的博客《%s》审核未通过。", blogTitle);
                break;
            case 3: // 整改中 (BlogStatusConstant.IN_REVIEW)
                baseMessage = String.format("您的博客《%s》需要整改。", blogTitle);
                break;
            case 4: // 已封禁 (BlogStatusConstant.PERMANENTLY_BANNED)
                baseMessage = String.format("您的博客《%s》因违反社区规范已被封禁。如有异议，请联系管理员申诉。", blogTitle);
                break;
            default:
                return null; // 其他状态不需要通知
        }
        // 如果有原因说明则附加到消息中
        if (statusMessage != null && !statusMessage.trim().isEmpty()) {
            return baseMessage + "原因：" + statusMessage.trim();
        }
        return baseMessage;
    }

    /**
     * 根据博客id查询博客详情（集成缓存）
     * 
     * @param id 博客ID
     * @return 博客详情
     */
    @Override
    public BlogVO getById(Long id) {
        log.debug("开始查询博客详情，博客ID：{}", id);

        // 1. 先尝试从缓存获取
        try {
            return advancedCacheManager.getWithProtection(
                    "blog:" + id,
                    () -> {
                        // 缓存未命中时从数据库加载
                        return loadBlogFromDatabase(id);
                    },
                    30,
                    java.util.concurrent.TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("缓存获取博客详情失败，降级到数据库查询，博客ID：{}", id, e);
            return loadBlogFromDatabase(id);
        }
    }

    /**
     * 从数据库加载博客详情（优化版本）
     * 
     * @param id 博客ID
     * @return 博客详情
     */
    private BlogVO loadBlogFromDatabase(Long id) {
        log.debug("从数据库加载博客详情，博客ID：{}", id);

        Blog blog = blogMapper.getById(id);
        if (blog == null) {
            log.warn("博客不存在，博客ID：{}", id);
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }

        BlogVO blogVO = new BlogVO();
        BeanUtils.copyProperties(blog, blogVO);

        try {
            //  优化：使用并行查询来获取附加信息
            CompletableFuture<User> userFuture = CompletableFuture
                    .supplyAsync(() -> userMapper.getById(blog.getUserId()));

            CompletableFuture<List<String>> tagListFuture = CompletableFuture
                    .supplyAsync(() -> blogTagMapper.getByBlogId(id));

            CompletableFuture<List<String>> customTagListFuture = CompletableFuture
                    .supplyAsync(() -> blogCustomTagMapper.getByBlogId(id));

            CompletableFuture<Category> categoryFuture = CompletableFuture
                    .supplyAsync(() -> categoryMapper.getById(blog.getCategoryId()));

            // 等待所有查询完成
            CompletableFuture.allOf(userFuture, tagListFuture, customTagListFuture, categoryFuture)
                    .join();

            // 设置作者信息
            User user = userFuture.get();
            if (user != null) {
                blogVO.setUsername(user.getUsername());
                blogVO.setAvatar(user.getAvatar());
            }

            // 设置标签信息
            List<String> tagList = tagListFuture.get();
            List<String> customTagList = customTagListFuture.get();
            log.debug("查询博客详情 - 博客ID：{}，关联标签：{}个，自定义标签：{}个",
                    id,
                    tagList != null ? tagList.size() : 0,
                    customTagList != null ? customTagList.size() : 0);

            blogVO.setTagList(tagList != null ? tagList : Collections.emptyList());
            blogVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());

            // 设置分类信息
            Category category = categoryFuture.get();
            if (category != null) {
                blogVO.setCategory(category.getName());
            }

            log.info("成功加载博客详情，博客ID：{}，标题：{}", id, blog.getTitle());
        } catch (Exception e) {
            log.error("获取博客附属信息失败，博客ID：{}，错误信息：{}", id, e.getMessage(), e);
        }

        return blogVO;
    }

    /**
     * 根据id删除博客（清除缓存）
     * 
     * @param ids 博客ID
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        log.info("开始批量删除博客，博客ID列表：{}", ids);
        if (ids == null || ids.isEmpty()) {
            log.warn("博客ID列表为空，批量删除操作已跳过");
            return;
        }

        try {
            // 遍历ID列表，并调用单个删除方法
            // 这样可以重用已有的事务和缓存清理逻辑
            for (Long id : ids) {
                deleteById(id);
            }
            log.info("批量删除博客成功");
        } catch (Exception e) {
            log.error("批量删除博客失败，错误信息：{}", e.getMessage(), e);
            // 抛出运行时异常以触发事务回滚
            throw new RuntimeException("批量删除博客失败");
        }
    }

    /**
     * 根据id删除博客（清除缓存）
     * 
     * @param id 博客ID
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("开始删除博客，博客ID：{}", id);

        try {
            // 删除标签
            blogTagMapper.deleteByBlogId(id);
            blogCustomTagMapper.deleteByBlogId(id);
            // 删除评论
            commentMapper.deleteByBlogId(id);
            // 删除博客
            blogMapper.deleteById(id);

            // 清除相关缓存
            evictBlogCache(id);

            log.info("博客删除成功，博客ID：{}", id);
        } catch (Exception e) {
            log.error("博客删除失败，博客ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new RuntimeException("博客删除失败");
        }
    }

    /**
     * 获取作者id
     * 
     * @param id
     * @return
     */
    @Override
    public Long getUserId(Long id) {
        return blogMapper.getUserId(id);
    }

    /**
     * 更新博客状态
     * 
     * @param id
     * @param status
     */
    @Override
    public void setIsTop(Long id, Integer status) {
        if (status == 1) {
            Blog blog = blogMapper.getById(id);
            if (!blog.getStatus().equals(BlogStatusConstant.APPROVED))
                throw new RuntimeException("非正常状态下的博客不能置顶");
        }
        blogMapper.setIsTop(id, status);
    }

    /**
     * 查看博客详情并记录浏览量（集成缓存，实时更新优化）
     * 
     * @param id     博客ID
     * @param userId 用户ID
     * @param ip     用户IP地址
     * @return 博客详情
     */
    @Override
    public BlogVO viewBlogDetail(Long id, Long userId, String ip) {
        log.debug("查看博客详情，博客ID：{}，用户ID：{}，IP：{}", id, userId, ip);

        // 1. 获取博客详情（使用缓存）
        BlogVO blogVO = getById(id);

        // 2. 同步记录浏览量以确保前端显示最新数据
        // 之前是异步的，导致前端拿到的是旧数据。现在为了用户体验（立即看到+1），改为同步（或者我们等待结果）
        try {
            boolean increased = viewService.recordView(id, userId, ip);
            if (increased) {
                // 如果浏览量增加了，手动更新返回对象的浏览量，而不必重新查询数据库
                blogVO.setViews(blogVO.getViews() + 1);
                log.debug("浏览量增加，手动更新返回数据中的views为: {}", blogVO.getViews());
            }
        } catch (Exception e) {
            log.warn("更新博客浏览量失败，博客ID：{}，用户ID：{}，IP：{}，错误信息：{}", id, userId, ip, e.getMessage());
        }

        return blogVO;
    }

    /**
     * 清除博客相关缓存
     * 
     * @param blogId 博客ID
     */
    private void evictBlogCache(Long blogId) {
        try {
            // 清除主缓存
            blogCacheService.evictBlogCache(blogId);

            // 可能还需要清除相关的列表缓存
            // blogCacheService.evictHotBlogsCache();
            // blogCacheService.evictCategoryBlogsCache(categoryId);

            log.debug("博客缓存清除成功，博客ID：{}", blogId);
        } catch (Exception e) {
            log.warn("清除博客缓存失败，博客ID：{}，错误信息：{}", blogId, e.getMessage());
            // 缓存清除失败不应该影响业务流程
        }
    }
}

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
import java.util.stream.Collectors;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:13
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

    /**
     * 分页查询博客文章
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
            log.info("blogTagMap：{}", blogTagMap);
            log.info("blogCustomTagMap：{}", blogCustomTagMap);

            // 组装数据
//            for (BlogVO blogVO : blogVOList) {
//                blogVO.setTagList(blogTagMap.getOrDefault(blogVO.getId(), Collections.emptyList()));
//                blogVO.setCustomTagList(blogCustomTagMap.getOrDefault(blogVO.getId(), Collections.emptyList()));
//            }

//            Map<String, List<String>> listMap = (Map<String, List<String>>) blogTagMap.get(1L);
//            List<String> list = listMap.get("value");
//            System.out.println("list:"+ list);

            // 组装数据
            for (BlogVO blogVO : blogVOList) {
                // 处理标签
                Map<String, List<String>> tagListMap = (Map<String, List<String>>) blogTagMap.get(blogVO.getId());
                List<String> tagList = tagListMap != null ? tagListMap.get("value") : Collections.emptyList();

                // 处理自定义标签
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTagMap.get(blogVO.getId());
                List<String> customTagList = customTagListMap != null ? customTagListMap.get("value") : Collections.emptyList();

                // 调试打印信息
//                log.info("blog_id:{}", blogVO.getId());
//                log.info("tagListMap:{}", tagListMap);
//                log.info("customTagListMap:{}", customTagListMap);
//                log.info("tagList:{}", tagList);
//                log.info("customTagList:{}", customTagList);

                // 设置标签列表到blogVO对象
                blogVO.setTagList(tagList != null ? tagList : Collections.emptyList());
                blogVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());
            }

        }

        return new PageResult(total,blogVOList);
    }

    /**
     * 新增博客文章
     * @param blogAddDTO
     */
    @Override
    @Transactional
    public void add(BlogAddDTO blogAddDTO) {
        // 应用XSS清理
        blogAddDTO.setTitle(XssCleanUtil.cleanAllTags(blogAddDTO.getTitle()));
        blogAddDTO.setContent(XssCleanUtil.cleanRichText(blogAddDTO.getContent()));

        // 检测到恶意脚本，清理后如果为空
        if (blogAddDTO.getTitle().isEmpty()) throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogAddDTO.getContent().isEmpty()) throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

        // 复制属性
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogAddDTO, blog);

//        if (userMapper.getById(blog.getUserId()) == null) throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);
        if (categoryMapper.getById(blog.getCategoryId()) == null) throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);

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
    }

    /**
     * 编辑博客
     * @param blogUpdateDTO
     */
    @Override
    @Transactional
    public void update(BlogUpdateDTO blogUpdateDTO) {
        // 清理XSS
        blogUpdateDTO.setTitle(XssCleanUtil.cleanAllTags(blogUpdateDTO.getTitle()));
        blogUpdateDTO.setContent(XssCleanUtil.cleanRichText(blogUpdateDTO.getContent()));

        // 清理结果
        if (blogUpdateDTO.getTitle().isEmpty()) throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogUpdateDTO.getContent().isEmpty()) throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

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
    }

    /**
     * 根据博客id查询博客详情
     * @param id
     * @return
     */
    @Override
    public BlogVO getById(Long id) {
        Blog blog = blogMapper.getById(id);
        if (blog == null) throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);

        BlogVO blogVO = new BlogVO();
        BeanUtils.copyProperties(blog, blogVO);

        try {
            // 作者
            User user = userMapper.getById(blog.getUserId());
            if (user != null) {
                blogVO.setUsername(user.getUsername());
                blogVO.setAvatar(user.getAvatar());
            }
    
            // 标签
            List<String> tagList = blogTagMapper.getByBlogId(id);
            List<String> customTagList = blogCustomTagMapper.getByBlogId(id);
            log.info("博客ID：{}，标签列表：{}，自定义标签列表：{}", id, tagList, customTagList);
            
            blogVO.setTagList(tagList != null ? tagList : Collections.emptyList());
            blogVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());
    
            // 分类
            Category category = categoryMapper.getById(blog.getCategoryId());
            if (category != null) {
                blogVO.setCategory(category.getName());
            }
        } catch (Exception e) {
            log.error("获取博客详情时出错：", e);
        }

        return blogVO;
    }

    /**
     * 根据id删除博客
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 删除标签
        blogTagMapper.deleteByBlogId(id);
        blogCustomTagMapper.deleteByBlogId(id);
        // 删除评论
        commentMapper.deleteByBlogId(id);
        // 删除博客
        blogMapper.deleteById(id);
    }

    /**
     * 获取作者id
     * @param id
     * @return
     */
    @Override
    public Long getUserId(Long id) {
        return blogMapper.getUserId(id);
    }

    /**
     * 更新博客状态
     * @param id
     * @param status
     */
    @Override
    public void setIsTop(Long id, Integer status) {
        if (status == 1) {
            Blog blog = blogMapper.getById(id);
            if (!blog.getStatus().equals(BlogStatusConstant.APPROVED)) throw new RuntimeException("非正常状态下的博客不能置顶");
        }
        blogMapper.setIsTop(id, status);
    }

    /**
     * 查看博客详情并记录浏览量
     * 
     * @param id 博客ID
     * @param userId 用户ID
     * @return 博客详情
     */
    @Override
    @Transactional
    public BlogVO viewBlogDetail(Long id, Long userId) {
        // 1. 获取博客详情
        BlogVO blogVO = getById(id);
        
        // 2. 记录浏览量（如果用户未登录则不记录）
        if (userId != null) {
            try {
                // 记录用户浏览
                viewService.recordView(id, userId);
            } catch (Exception e) {
                log.error("记录博客浏览量失败", e);
                // 不影响主流程，继续返回博客详情
            }
        }
        
        return blogVO;
    }
}

package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.CollectionMapper;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.Collection;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.CollectionVO;
import com.blog.result.PageResult;
import com.blog.service.CollectionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogCacheServiceImpl blogCacheService; // 博客缓存服务，用于收藏后清除缓存

    @Override
    @Transactional
    public void collectBlog(Long blogId, Long userId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }

        // 2. 检查是否已经收藏
        Collection collection = collectionMapper.findByUserIdAndBlogId(userId, blogId);

        if (collection != null) {
            // 已收藏，取消收藏
            int rows = collectionMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户取消收藏 - 用户ID：{}，博客ID：{}", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNCOLLECT_FAIL);
            }

            // 更新博客收藏数量
            updateBlogCollectionCount(blogId);
        } else {
            // 未收藏，添加收藏
            Collection newCollection = Collection.builder()
                    .userId(userId)
                    .blogId(blogId)
                    .collectTime(LocalDateTime.now())
                    .build();

            int rows = collectionMapper.insert(newCollection);
            log.info("用户收藏博客 - 用户ID：{}，博客ID：{}", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.COLLECT_FAIL);
            }

            // 更新博客收藏数量
            updateBlogCollectionCount(blogId);
        }
    }

    @Override
    public List<CollectionVO> getCollectionList(Long userId) {
        // 1. 获取用户收藏的博客列表
        List<Collection> collections = collectionMapper.findByUserId(userId);
        List<CollectionVO> collectionVOS = new ArrayList<>();

        // 2. 转换为VO对象
        for (Collection collection : collections) {
            // 获取博客信息
            Blog blog = blogMapper.getById(collection.getBlogId());
            if (blog == null) {
                continue;
            }

            // 获取作者信息
            User author = userMapper.getById(blog.getUserId());
            if (author == null) {
                continue;
            }

            // 获取分类信息
            Category category = categoryMapper.getById(blog.getCategoryId());
            String categoryName = category != null ? category.getName() : "未分类";

            // 构建VO对象
            CollectionVO vo = CollectionVO.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .userId(author.getId())
                    .username(author.getUsername())
                    .avatar(author.getAvatar())
                    .categoryName(categoryName)
                    .views(blog.getViews())
                    .likes(blog.getLikes())
                    .dislikes(blog.getDislikes())
                    .collections(blog.getCollections())
                    .collectTime(collection.getCollectTime())
                    .build();

            collectionVOS.add(vo);
        }

        return collectionVOS;
    }

    @Override
    public PageResult getCollectionPage(BlogPageQueryDTO blogPageQueryDTO) {
        // 设置分页参数
        int pageNum = blogPageQueryDTO.getPage() != null ? blogPageQueryDTO.getPage() : 1;
        int pageSize = blogPageQueryDTO.getPageSize() != null ? blogPageQueryDTO.getPageSize() : 10;

        // 开始分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<CollectionVO> page = collectionMapper.pageByUserId(blogPageQueryDTO);

        // 获取总记录数和查询结果
        long total = page.getTotal();
        List<CollectionVO> records = page.getResult();

        // 返回分页结果
        return new PageResult(total, records);
    }

    @Override
    public boolean isCollected(Long blogId, Long userId) {
        return collectionMapper.findByUserIdAndBlogId(userId, blogId) != null;
    }

    /**
     * 更新博客收藏数量
     * 
     * @param blogId 博客id
     */
    private void updateBlogCollectionCount(Long blogId) {
        try {
            // 判断操作是添加还是删除收藏，根据collectionMapper中的记录确定
            Collection collection = collectionMapper.findByUserIdAndBlogId(BaseContext.getCurrentId(), blogId);

            if (collection != null) {
                // 添加了收藏，增加收藏计数
                blogMapper.incrementCollections(blogId);
                log.info("博客收藏量增加 - 博客ID：{}", blogId);
            } else {
                // 取消了收藏，减少收藏计数
                blogMapper.decrementCollections(blogId);
                log.info("博客收藏量减少 - 博客ID：{}", blogId);
            }

            // 清除博客缓存，确保下次获取博客详情时能读取到最新的收藏数
            blogCacheService.evictBlogCache(blogId);
            log.info("已清除博客{}的缓存", blogId);
        } catch (Exception e) {
            log.error("更新博客收藏数量失败，博客ID：{}，错误信息：{}", blogId, e.getMessage());
        }
    }
}
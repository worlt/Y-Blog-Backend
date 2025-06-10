package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.UserMapper;
import com.blog.mapper.ViewMapper;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.User;
import com.blog.pojo.entity.View;
import com.blog.pojo.vo.BlogVO;
import com.blog.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览记录服务实现类
 *
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewServiceImpl implements ViewService {

    @Autowired
    private ViewMapper viewMapper;
    
    @Autowired
    private BlogMapper blogMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 记录用户浏览博客操作
     * 同一用户对同一博客只记录一次浏览量
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void recordView(Long blogId, Long userId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }
        
        // 2. 检查用户是否已经浏览过该博客
        View existingView = viewMapper.findByUserIdAndBlogId(userId, blogId);
        if (existingView == null) {
            // 3. 如果没有浏览记录，添加记录
            View view = View.builder()
                    .userId(userId)
                    .blogId(blogId)
                    .viewTime(LocalDateTime.now())
                    .build();
                    
            int rows = viewMapper.insert(view);
            log.info("用户{}浏览了博客{}", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException("记录浏览失败");
            }
            
            // 4. 更新博客浏览量
            updateBlogViewCount(blogId);
        } else {
            // 如果浏览过
            View view = View.builder()
                    .id(existingView.getId())
                    .userId(existingView.getUserId())
                    .blogId(existingView.getBlogId())
                    .viewTime(LocalDateTime.now())
                    .build();
            viewMapper.update(view);
        }

    }

    /**
     * 获取用户的浏览历史
     * 
     * @param userId 用户ID
     * @return 博客VO列表
     */
    @Override
    public List<BlogVO> getUserViewHistory(Long userId) {
        // 1. 获取用户浏览的博客记录
        List<View> views = viewMapper.findByUserId(userId);
        List<BlogVO> blogVOS = new ArrayList<>();
        
        // 2. 转换为BlogVO对象
        for (View view : views) {
            Blog blog = blogMapper.getById(view.getBlogId());
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
            
            // 构建BlogVO对象
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            blogVO.setUsername(author.getUsername());
            blogVO.setAvatar(author.getAvatar());
            blogVO.setCategory(categoryName);
            
            blogVOS.add(blogVO);
        }
        
        return blogVOS;
    }

    /**
     * 检查用户是否已浏览过博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @return 是否已浏览
     */
    @Override
    public boolean hasViewed(Long blogId, Long userId) {
        return viewMapper.findByUserIdAndBlogId(userId, blogId) != null;
    }
    
    /**
     * 更新博客浏览量
     * 
     * @param blogId 博客ID
     */
    private void updateBlogViewCount(Long blogId) {
        try {
            // 使用增量更新方式直接更新博客浏览量
            blogMapper.incrementViews(blogId);
            log.info("更新博客{}浏览量+1", blogId);
        } catch (Exception e) {
            log.error("更新博客浏览量失败", e);
        }
    }
} 
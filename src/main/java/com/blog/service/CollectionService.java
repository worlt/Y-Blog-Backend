package com.blog.service;

import com.blog.pojo.vo.CollectionVO;

import java.util.List;

public interface CollectionService {
    
    /**
     * 收藏/取消收藏博客
     * @param blogId 博客id
     * @param userId 用户id
     */
    void collectBlog(Long blogId, Long userId);
    
    /**
     * 查询用户收藏的博客列表
     * @param userId 用户id
     * @return 收藏博客列表
     */
    List<CollectionVO> getCollectionList(Long userId);
    
    /**
     * 检查用户是否收藏了指定博客
     * @param blogId 博客id
     * @param userId 用户id
     * @return 是否收藏
     */
    boolean isCollected(Long blogId, Long userId);
} 
package com.blog.service;

import com.blog.pojo.dto.TagDTO;
import com.blog.pojo.entity.Tag;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface TagService {
    
    /**
     * 新增标签
     * @param tagDTO 标签信息
     */
    void add(TagDTO tagDTO);
    
    /**
     * 更新标签
     * @param tagDTO 标签信息
     */
    void update(TagDTO tagDTO);
    
    /**
     * 删除标签
     * @param id 标签ID
     */
    void deleteById(Long id);
    
    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签对象
     */
    Tag getById(Long id);
    
    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> listAll();
} 
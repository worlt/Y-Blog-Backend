package com.blog.service;

import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:13
 */
public interface BlogService {

    /**
     * 博客文章分页查询
     *
     * @param blogPageQueryDTO
     * @return
     */
    PageResult pageQuery(BlogPageQueryDTO blogPageQueryDTO);

    /**
     * 新增博客文章
     * @param blogAddDTO
     */
    void add(BlogAddDTO blogAddDTO);

    /**
     * 编辑博客
     * @param blogUpdateDTO
     */
    void update(BlogUpdateDTO blogUpdateDTO);
}

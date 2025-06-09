package com.blog.service;

import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:12
 */
public interface BlogService {

    /**
     * 分页查询博客文章
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
     * 博客详情
     * @param id 博客ID
     * @return 博客详情
     */
    BlogVO getById(Long id);

    /**
     * 查看博客详情并记录浏览量
     * @param id 博客ID
     * @param userId 用户ID
     * @return 博客详情
     */
    BlogVO viewBlogDetail(Long id, Long userId);

    /**
     * 编辑博客
     * @param blogUpdateDTO
     */
    void update(BlogUpdateDTO blogUpdateDTO);

    /**
     * 删除博客
     * @param id
     */
    void deleteById(Long id);

    /**
     * 获取作者id
     * @param id
     * @return
     */
    Long getUserId(Long id);
}

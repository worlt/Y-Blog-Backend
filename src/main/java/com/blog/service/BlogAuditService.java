package com.blog.service;

import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.vo.BlogAuditVO;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:22
 */
public interface BlogAuditService {

    /**
     * 分页查询博客审核
     * @param blogAuditPageQueryDTO
     * @return
     */
    PageResult pageQuery(BlogAuditPageQueryDTO blogAuditPageQueryDTO);

    /**
     * 根据id查询审核博客详情
     * @param id
     * @return
     */
    BlogAuditVO getById(Long id);

    /**
     * 审核博客
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);
}

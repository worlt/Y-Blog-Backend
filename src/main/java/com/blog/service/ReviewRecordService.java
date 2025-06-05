package com.blog.service;

import com.blog.pojo.dto.ReviewRecordPageQueryDTO;
import com.blog.pojo.vo.ReviewRecordVO;
import com.blog.result.PageResult;

/**
 * 审核记录服务接口
 * @Author worlt
 * @Date 2025/6/4 下午9:50
 */
public interface ReviewRecordService {
    
    /**
     * 分页查询审核记录
     * 支持多种查询场景
     * @param reviewRecordPageQueryDTO
     * @return
     */
    PageResult pageQuery(ReviewRecordPageQueryDTO reviewRecordPageQueryDTO);
    
    /**
     * 根据id查询审核记录详情
     * @param id
     * @return
     */
    ReviewRecordVO getById(Long id);
} 
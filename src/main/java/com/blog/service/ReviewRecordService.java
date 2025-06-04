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
    
    /**
     * 根据类型和关联ID查询审核记录列表
     * @param reviewType 审核类型
     * @param targetId 关联ID
     * @param page 页码
     * @param pageSize 每页记录数
     * @return
     */
    PageResult pageQueryByTypeAndTargetId(Integer reviewType, Long targetId, Integer page, Integer pageSize);
    
    /**
     * 根据审核员ID查询审核记录
     * @param reviewerId 审核员ID
     * @param page 页码
     * @param pageSize 每页记录数
     * @return
     */
    PageResult pageQueryByReviewerId(Long reviewerId, Integer page, Integer pageSize);
} 
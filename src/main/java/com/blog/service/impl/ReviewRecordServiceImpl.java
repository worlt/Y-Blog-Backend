package com.blog.service.impl;

import com.blog.mapper.ReviewRecordMapper;
import com.blog.pojo.dto.ReviewRecordPageQueryDTO;
import com.blog.pojo.entity.ReviewRecord;
import com.blog.pojo.vo.ReviewRecordVO;
import com.blog.result.PageResult;
import com.blog.service.ReviewRecordService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 审核记录服务实现类
 * @Author worlt
 * @Date 2025/6/4 下午10:00
 */
@Service
@Slf4j
public class ReviewRecordServiceImpl implements ReviewRecordService {

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    /**
     * 分页查询审核记录
     * @param reviewRecordPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ReviewRecordPageQueryDTO reviewRecordPageQueryDTO) {
        PageHelper.startPage(reviewRecordPageQueryDTO.getPage(), reviewRecordPageQueryDTO.getPageSize());
        Page<ReviewRecordVO> page = reviewRecordMapper.pageQuery(reviewRecordPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询审核记录详情
     * @param id
     * @return
     */
    @Override
    public ReviewRecordVO getById(Long id) {
        return reviewRecordMapper.getById(id);
    }

    /**
     * 根据类型和关联ID查询审核记录列表
     * @param reviewType 审核类型
     * @param targetId   关联ID
     * @param page       页码
     * @param pageSize   每页记录数
     * @return
     */
    @Override
    public PageResult pageQueryByTypeAndTargetId(Integer reviewType, Long targetId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<ReviewRecordVO> pageData = reviewRecordMapper.pageQueryByTypeAndTargetId(reviewType, targetId);
        return new PageResult(pageData.getTotal(), pageData.getResult());
    }

    /**
     * 根据审核员ID查询审核记录
     * @param reviewerId 审核员ID
     * @param page       页码
     * @param pageSize   每页记录数
     * @return
     */
    @Override
    public PageResult pageQueryByReviewerId(Long reviewerId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<ReviewRecordVO> pageData = reviewRecordMapper.pageQueryByReviewerId(reviewerId);
        return new PageResult(pageData.getTotal(), pageData.getResult());
    }
} 
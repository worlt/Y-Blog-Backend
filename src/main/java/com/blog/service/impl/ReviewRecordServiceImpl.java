package com.blog.service.impl;

import com.blog.constant.ReviewType;
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
     * 支持多种查询场景
     * @param reviewRecordPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ReviewRecordPageQueryDTO reviewRecordPageQueryDTO) {
        // 如果recordId存在，直接调用getById方法
        if (reviewRecordPageQueryDTO.getRecordId() != null) {
            ReviewRecordVO reviewRecordVO = reviewRecordMapper.getById(reviewRecordPageQueryDTO.getRecordId());
            return new PageResult(1L, reviewRecordVO != null ? java.util.Collections.singletonList(reviewRecordVO) : java.util.Collections.emptyList());
        }

        // 处理目标ID转换
        if (reviewRecordPageQueryDTO.getTargetId() != null && reviewRecordPageQueryDTO.getReviewType() != null) {
            switch (reviewRecordPageQueryDTO.getReviewType()) {
                case 0: // 用户
                    reviewRecordPageQueryDTO.setUserId(reviewRecordPageQueryDTO.getTargetId());
                    break;
                case 1: // 博客
                    reviewRecordPageQueryDTO.setBlogId(reviewRecordPageQueryDTO.getTargetId());
                    break;
                case 2: // 评论
                    reviewRecordPageQueryDTO.setCommentId(reviewRecordPageQueryDTO.getTargetId());
                    break;
                case 3: // 申诉 - 也是用户ID
                    reviewRecordPageQueryDTO.setUserId(reviewRecordPageQueryDTO.getTargetId());
                    break;
            }
        }
        
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

} 
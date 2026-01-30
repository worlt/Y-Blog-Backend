package com.blog.service;

import com.blog.pojo.dto.CommentAddReportDTO;
import com.blog.pojo.dto.CommentReportDTO;
import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.vo.CommentReportVO;
import com.blog.result.PageResult;

/**
 * 评论举报服务接口
 * 
 * @Author worlt
 * @Date 2025/6/4 下午6:00
 */
public interface CommentReportService {

    /**
     * 分页查询
     * 
     * @param commentReportPageQueryDTO
     * @return
     */
    PageResult pageQuery(CommentReportPageQueryDTO commentReportPageQueryDTO);

    /**
     * 根据id查询举报详情
     * 
     * @param id
     * @return
     */
    CommentReportVO getById(Long id);

    /**
     * 处理举报
     * 
     * @param commentReportDTO
     */
    void handle(CommentReportDTO commentReportDTO);

    /**
     * 更新举报状态
     * 
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 根据id删除举报
     * 
     * @param id
     */
    void deleteById(Long id);

    /**
     * 批量删除举报
     * 
     * @param ids
     */
    void deleteByIds(Long[] ids);

    /**
     * 用户举报评论
     * 
     * @param commentAddReportDTO
     */
    void add(CommentAddReportDTO commentAddReportDTO);

    /**
     * 标记为处理中（同时更新同一评论的其他举报）
     * 
     * @param id         举报ID
     * @param reviewerId 审核员ID
     */
    void markAsInProcess(Long id, Long reviewerId);
}
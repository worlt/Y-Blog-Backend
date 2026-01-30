package com.blog.service;

import com.blog.pojo.dto.UserAddReportDTO;
import com.blog.pojo.dto.UserReportDTO;
import com.blog.pojo.dto.UserReportPageQueryDTO;
import com.blog.pojo.vo.UserReportVO;
import com.blog.result.PageResult;

/**
 * 用户举报服务接口
 * 
 * @Author worlt
 * @Date 2025/6/4 下午2:30
 */
public interface UserReportService {

    /**
     * 分页查询
     * 
     * @param userReportPageQueryDTO
     * @return
     */
    PageResult pageQuery(UserReportPageQueryDTO userReportPageQueryDTO);

    /**
     * 根据id查询举报详情
     * 
     * @param id
     * @return
     */
    UserReportVO getById(Long id);

    /**
     * 处理举报
     * 
     * @param userReportDTO
     */
    void handle(UserReportDTO userReportDTO);

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
     * 举报用户
     * 
     * @param userAddReportDTO
     */
    void add(UserAddReportDTO userAddReportDTO);

    /**
     * 标记为处理中（同时更新同一用户的其他举报）
     * 
     * @param id         举报ID
     * @param reviewerId 审核员ID
     */
    void markAsInProcess(Long id, Long reviewerId);
}

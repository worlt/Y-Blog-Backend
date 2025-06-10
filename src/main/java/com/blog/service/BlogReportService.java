package com.blog.service;

import com.blog.pojo.dto.BlogAddReportDTO;
import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.BlogReportPageQueryDTO;
import com.blog.pojo.vo.BlogReportVO;
import com.blog.result.PageResult;

/**
 * 博客举报服务接口
 * @Author worlt
 * @Date 2025/6/3 下午5:45
 */
public interface BlogReportService {

    /**
     * 分页查询
     * @param blogReportPageQueryDTO
     * @return
     */
    PageResult pageQuery(BlogReportPageQueryDTO blogReportPageQueryDTO);

    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    BlogReportVO getById(Long id);

    /**
     * 处理举报
     * @param blogReportDTO
     */
    void handle(BlogReportDTO blogReportDTO);

    /**
     * 更新举报状态
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 根据id删除举报
     * @param id
     */
    void deleteById(Long id);

    /**
     * 批量删除举报
     * @param ids
     */
    void deleteByIds(Long[] ids);

    /**
     * 新增博客举报
     * @param blogAddReportDTO
     */
    void add(BlogAddReportDTO blogAddReportDTO);
}

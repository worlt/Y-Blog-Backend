package com.blog.service;

import com.blog.pojo.dto.AppealDTO;
import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.entity.Appeal;
import com.blog.pojo.vo.AppealVO;
import com.blog.result.PageResult;

/**
 * 申诉服务接口
 * 
 * @Author worlt
 * @Date 2025/6/4 下午9:20
 */
public interface AppealService {

    /**
     * 标记为处理中
     * 
     * @param id
     * @param reviewerId
     */
    void markAsInProcess(Long id, Long reviewerId);

    /**
     * 分页查询
     * 
     * @param appealPageQueryDTO
     * @return
     */
    PageResult pageQuery(AppealPageQueryDTO appealPageQueryDTO);

    /**
     * 根据id查询申诉详情
     * 
     * @param id
     * @return
     */
    AppealVO getById(Long id);

    /**
     * 处理申诉
     * 
     * @param appealDTO
     */
    void handle(AppealDTO appealDTO);

    /**
     * 更新申诉状态
     * 
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 根据id删除申诉
     * 
     * @param id
     */
    void deleteById(Long id);

    /**
     * 批量删除申诉
     * 
     * @param ids
     */
    void deleteByIds(Long[] ids);

    /**
     * 提交申诉
     * 
     * @param appeal
     */
    void submit(Appeal appeal);
}
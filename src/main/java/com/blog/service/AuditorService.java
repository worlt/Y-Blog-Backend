package com.blog.service;

import com.blog.pojo.dto.AuditorDTO;
import com.blog.pojo.dto.AuditorPageQueryDTO;
import com.blog.pojo.vo.AuditorVO;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/6/2 下午12:57
 */
public interface AuditorService {

    /**
     * 分页查询审核员
     * @param auditorService
     * @return
     */
    PageResult page(AuditorPageQueryDTO auditorService);

    /**
     * 添加审核员
     * @param auditorDTO
     */
    void add(AuditorDTO auditorDTO);

    /**
     * 根据id删除审核员
     * @param id
     */
    void deleteById(Long id);

    /**
     * 编辑审核员
     * @param auditorDTO
     */
    void update(AuditorDTO auditorDTO);

    /**
     * 根据id查询审核员
     * @param id
     * @return
     */
    AuditorVO getById(Long id);
}

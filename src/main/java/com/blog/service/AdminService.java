package com.blog.service;

import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.entity.Admin;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/18 下午7:48
 */
public interface AdminService {

    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    Admin login(AdminLoginDTO adminLoginDTO);

    /**
     * 新增管理员
     * @param adminDTO
     */
    void add(AdminDTO adminDTO);

    /**
     * 分页查询
     * @param adminPageQueryDTO
     * @return
     */
    PageResult pageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     * 启用或禁用管理员
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);
}

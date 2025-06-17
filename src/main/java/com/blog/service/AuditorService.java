package com.blog.service;

import com.blog.pojo.dto.*;
import com.blog.pojo.entity.Auditor;
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

    /**
     * 审核员登录
     * @param auditorLoginDTO
     * @return
     */
    Auditor login(AuditorLoginDTO auditorLoginDTO);

    /**
     * 设置审核员状态
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 修改密码
     * @param auditorPasswordDTO
     */
    void updatePassword(AuditorPasswordDTO auditorPasswordDTO);

    /**
     * 通过邮箱验证码重置密码
     * @param auditorResetPasswordDTO
     */
    void resetPassword(AuditorResetPasswordDTO auditorResetPasswordDTO);
}

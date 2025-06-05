package com.blog.service;

import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserEmailLoginDTO;
import com.blog.pojo.dto.UserEmailRegisterDTO;
import com.blog.pojo.dto.ResetPasswordDTO;
import com.blog.pojo.dto.UserPageQueryDTO;
import com.blog.pojo.entity.User;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/20 下午10:22
 */
public interface UserService {

    /**
     * 用户分页查询
     * @param userPAgeQueryDTO
     * @return
     */
    PageResult pageQuery(UserPageQueryDTO userPAgeQueryDTO);

    /**
     * 新增用户
     * @param userDTO
     */
    void add(UserDTO userDTO);

    /**
     * 编辑用户信息
     * @param userDTO
     */
    void update(UserDTO userDTO);

    /**
     * 启用禁用用户
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 根据id删除用户
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User getById(Long id);
    
    /**
     * 邮箱验证码注册
     * @param userEmailRegisterDTO 注册信息
     */
    void registerByEmail(UserEmailRegisterDTO userEmailRegisterDTO);
    
    /**
     * 邮箱密码登录
     * @param userEmailLoginDTO 登录信息
     * @return 用户信息
     */
    User loginByPassword(UserEmailLoginDTO userEmailLoginDTO);
    
    /**
     * 邮箱验证码登录
     * @param email 邮箱
     * @return 用户信息
     */
    User loginByEmail(String email);
    
    /**
     * 重置密码
     * @param resetPasswordDTO 重置密码信息
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}

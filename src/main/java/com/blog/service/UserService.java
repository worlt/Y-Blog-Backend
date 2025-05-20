package com.blog.service;

import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserPAgeQueryDTO;
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
    PageResult pageQuery(UserPAgeQueryDTO userPAgeQueryDTO);

    /**
     * 新增用户
     * @param userDTO
     */
    void add(UserDTO userDTO);
}

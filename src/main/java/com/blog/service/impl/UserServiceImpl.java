package com.blog.service.impl;

import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserPAgeQueryDTO;
import com.blog.pojo.entity.User;
import com.blog.result.PageResult;
import com.blog.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/20 下午10:22
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户分页查询
     * @param userPAgeQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(UserPAgeQueryDTO userPAgeQueryDTO) {
        PageHelper.startPage(userPAgeQueryDTO.getPage(), userPAgeQueryDTO.getPageSize());
        Page<User> page = userMapper.pageQuery(userPAgeQueryDTO);
        Long total = page.getTotal();
        List<User> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 新增用户
     * @param userDTO
     */
    @Override
    public void add(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (user.getStatus() == null) user.setStatus(UserStatusConstant.NORMAL);
        if (user.getRole() == null) user.setRole(RoleConstant.USER);
        user.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        if (user.getFans() == null) user.setFans(0L);

        userMapper.add(user);
    }
}

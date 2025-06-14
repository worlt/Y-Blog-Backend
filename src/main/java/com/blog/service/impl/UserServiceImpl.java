package com.blog.service.impl;

import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.*;
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
     *
     * @param userPAgeQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPAgeQueryDTO) {
        PageHelper.startPage(userPAgeQueryDTO.getPage(), userPAgeQueryDTO.getPageSize());
        Page<User> page = userMapper.pageQuery(userPAgeQueryDTO);
        Long total = page.getTotal();
        List<User> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 新增用户
     *
     * @param userDTO
     */
    @Override
    public void add(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (user.getStatus() == null) user.setStatus(UserStatusConstant.NORMAL);
        if (user.getRole() == null) user.setRole(RoleConstant.USER);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        if (user.getFans() == null) user.setFans(0L);

        userMapper.add(user);
    }

    /**
     * 编辑用户信息
     *
     * @param userDTO
     */
    @Override
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (user.getRole().equals(RoleConstant.ADMIN) || user.getRole().equals(RoleConstant.AUDITOR))
            throw new RuntimeException("用户不能直接升级为管理员或审核员");


        if (user.getPassword() != null) {
            String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(pwd);
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 启用禁用用户
     *
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        userMapper.setStatus(id, status);
    }

    /**
     * 根据id删除用户
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    /**
     * 邮箱验证码注册
     *
     * @param userEmailRegisterDTO 注册信息
     */
    @Override
    public void registerByEmail(UserEmailRegisterDTO userEmailRegisterDTO) {
        // 检查邮箱是否已注册
        User existingUser = userMapper.getByEmail(userEmailRegisterDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 创建新用户
        User user = User.builder()
                .username(userEmailRegisterDTO.getUsername())
                .password(DigestUtils.md5DigestAsHex(userEmailRegisterDTO.getPassword().getBytes()))
                .email(userEmailRegisterDTO.getEmail())
                .role(RoleConstant.USER)
                .status(UserStatusConstant.NORMAL)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .fans(0L)
                .build();

        userMapper.add(user);
    }

    /**
     * 邮箱密码登录
     *
     * @param userEmailLoginDTO 登录信息
     * @return 用户信息
     */
    @Override
    public User loginByPassword(UserEmailLoginDTO userEmailLoginDTO) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(userEmailLoginDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 密码校验
        String encryptedPassword = DigestUtils.md5DigestAsHex(userEmailLoginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus().equals(UserStatusConstant.BANNED)) {
            throw new RuntimeException("账号已被封禁");
        }

        return user;
    }

    /**
     * 邮箱验证码登录
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public User loginByEmail(String email) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus().equals(UserStatusConstant.BANNED)) {
            throw new RuntimeException("账号已被封禁");
        }

        return user;
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDTO 重置密码信息
     */
    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(resetPasswordDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新密码
        user.setPassword(DigestUtils.md5DigestAsHex(resetPasswordDTO.getNewPassword().getBytes()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }
}

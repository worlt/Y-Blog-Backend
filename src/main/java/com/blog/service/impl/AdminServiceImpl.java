package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.mapper.AdminMapper;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.entity.Admin;
import com.blog.result.PageResult;
import com.blog.service.AdminService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/18 下午7:48
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     *
     * @param adminLoginDTO
     * @return
     */
    @Override

    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        // 1.根据用户名查询数据库中的数据
        Admin admin = adminMapper.getUsername(username);

        // 2.处理异常情况
        if (admin == null) {
            // 账号不存在
            throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码对比
        // 对比前端明文密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!(password.equals(admin.getPassword()))) {
            // 密码错误
            throw new RuntimeException(MessageConstant.PASSWORD_ERROR);
        }

        if (admin.getStatus() == UserStatusConstant.BANNED) {
            // 账号封禁
            throw new RuntimeException(MessageConstant.ACCOUNT_LOCKED);
        }

        return admin;
    }

    /**
     * 新增管理员
     * @param adminDTO
     */
    @Override
    public void add(AdminDTO adminDTO) {
        Admin admin = new Admin();
        // 拷贝属性
        BeanUtils.copyProperties(adminDTO, admin);
        // 设置状态为正常
        admin.setStatus(UserStatusConstant.NORMAL);
        // 设置默认密码 123456
        admin.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设为管理员
        admin.setRole(RoleConstant.ADMIN);
        // 设置创建时间和更新时间
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());

        adminMapper.insert(admin);
    }

    /**
     * 分页查询
     * @param adminPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(AdminPageQueryDTO adminPageQueryDTO) {
        PageHelper.startPage(adminPageQueryDTO.getPage(), adminPageQueryDTO.getPageSize());
        Page<Admin> page = adminMapper.pageQuery(adminPageQueryDTO);
        Long total = page.getTotal();
        List<Admin> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 启用或禁用管理员
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        // 判断是否 username 为 admin 管理员，如果是则无法禁用或启用
        Admin admin = adminMapper.getById(id);
        if (admin.getUsername().equals("admin")) {
            throw new RuntimeException("admin管理员无法被启用或禁用");
        }

        // 更新状态
        admin.setStatus(status);
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
    }

    /**
     * 编辑管理员信息
     * @param adminDTO
     */
    @Override
    public void update(AdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        if (!admin.getPassword().isEmpty()) {
            String pwd = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
            admin.setPassword(pwd);
        }
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
    }


}

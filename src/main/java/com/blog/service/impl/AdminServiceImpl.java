package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.AdminMapper;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.dto.AdminPasswordDTO;
import com.blog.pojo.dto.AdminResetPasswordDTO;
import com.blog.pojo.entity.Admin;
import com.blog.pojo.vo.AdminVO;
import com.blog.result.PageResult;
import com.blog.service.AdminService;
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
 * @Date 2025/5/18 下午7:48
 */
@Service
@Slf4j
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
        admin.setPassword(admin.getPassword().isEmpty()
                ? DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())
                : DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
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
        // 判断是否为 admin 或 system 管理员，如果是则无法禁用或启用
        if (id == 1L || id == 2L) throw new RuntimeException("admin或system管理员无法被启用或禁用");

        Admin admin = adminMapper.getById(id);

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
        // 判断是否为 admin 或 system 管理员，如果是则无法禁用或启用
        if (adminDTO.getId() == 1L || adminDTO.getId() == 2L) {
            if (!adminDTO.getId().equals(BaseContext.getCurrentId())) throw new RuntimeException("admin或system管理员无法被修改");
        }

        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        
        // 如果role不为空，才进行角色检查
        if (admin.getRole() != null && (admin.getRole().equals(RoleConstant.USER) || admin.getRole().equals(RoleConstant.VIP))) {
            throw new RuntimeException("管理员或审核员无法直接修改为用户");
        }
        
        // 如果密码不为空且不为空字符串，才进行密码加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String pwd = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
            admin.setPassword(pwd);
        }
        
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
    }

    /**
     * 根据id删除管理员
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        if (id == 1L || id == 2L) throw new RuntimeException("admin 和 system 管理员无法被删除");
        adminMapper.deleteById(id);
    }

    /**
     * 根据id查询管理员
     * @param id
     * @return
     */
    @Override
    public AdminVO getById(Long id) {
        Admin admin = adminMapper.getById(id);
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        return adminVO;
    }
    
    /**
     * 修改管理员密码
     * @param adminPasswordDTO
     */
    @Override
    public void updatePassword(AdminPasswordDTO adminPasswordDTO) {
        Long adminId = adminPasswordDTO.getId();
        
        // 获取管理员信息
        Admin admin = adminMapper.getById(adminId);
        if (admin == null) {
            throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 验证原密码
        String oldPassword = DigestUtils.md5DigestAsHex(adminPasswordDTO.getOldPassword().getBytes());
        if (!oldPassword.equals(admin.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 修改密码
        admin.setPassword(DigestUtils.md5DigestAsHex(adminPasswordDTO.getNewPassword().getBytes()));
        admin.setUpdateTime(LocalDateTime.now());
        
        // 更新管理员信息
        adminMapper.update(admin);
    }
    
    /**
     * 通过邮箱验证码重置密码
     * @param resetPasswordDTO
     */
    @Override
    public void resetPassword(AdminResetPasswordDTO resetPasswordDTO) {
        String email = resetPasswordDTO.getEmail();
        
        // 根据邮箱查找管理员账号
        Admin admin = adminMapper.getByEmail(email);
        if (admin == null) {
            throw new RuntimeException("该邮箱未绑定管理员账号");
        }
        
        // 重置密码
        admin.setPassword(DigestUtils.md5DigestAsHex(resetPasswordDTO.getNewPassword().getBytes()));
        admin.setUpdateTime(LocalDateTime.now());
        
        // 更新管理员信息
        adminMapper.update(admin);
    }
}

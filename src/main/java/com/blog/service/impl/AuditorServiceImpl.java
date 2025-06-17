package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.mapper.AuditorMapper;
import com.blog.pojo.dto.*;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorVO;
import com.blog.result.PageResult;
import com.blog.service.AuditorService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/6/2 下午12:57
 */
@Service
@Slf4j
public class AuditorServiceImpl implements AuditorService {

    @Autowired
    private AuditorMapper auditorMapper;

    /**
     * 分页查询审核员
     *
     * @param auditorPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(AuditorPageQueryDTO auditorPageQueryDTO) {
        PageHelper.startPage(auditorPageQueryDTO.getPage(), auditorPageQueryDTO.getPageSize());
        Page<AuditorVO> page = auditorMapper.page(auditorPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 添加审核员
     *
     * @param auditorDTO
     */
    @Override
    public void add(AuditorDTO auditorDTO) {
        Auditor auditor = new Auditor();
        BeanUtils.copyProperties(auditorDTO, auditor);
        auditor.setStatus(UserStatusConstant.NORMAL);

        auditor.setPassword(auditor.getPassword().isEmpty()
                ? DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())
                : DigestUtils.md5DigestAsHex(auditor.getPassword().getBytes()));
        auditor.setRole(RoleConstant.AUDITOR);
        auditor.setCreateTime(LocalDateTime.now());
        auditor.setUpdateTime(LocalDateTime.now());
        auditorMapper.add(auditor);
    }

    /**
     * 根据id删除审核员
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        auditorMapper.deleteById(id);
    }

    /**
     * 编辑审核员
     * @param auditorDTO
     */
    @Override
    public void update(AuditorDTO auditorDTO) {
        Auditor auditor = new Auditor();
        BeanUtils.copyProperties(auditorDTO, auditor);
        if (auditor.getPassword() != null && !auditor.getPassword().isEmpty()) auditor.setPassword(DigestUtils.md5DigestAsHex(auditor.getPassword().getBytes()));
        auditorMapper.update(auditor);
    }

    /**
     * 根据id查询审核员
     * @param id
     * @return
     */
    @Override
    public AuditorVO getById(Long id) {
        Auditor auditor = auditorMapper.getById(id);
        AuditorVO auditorVO = new AuditorVO();
        BeanUtils.copyProperties(auditor, auditorVO);
        return auditorVO;
    }

    /**
     * 审核员登录
     * @param auditorLoginDTO
     * @return
     */
    @Override
    public Auditor login(AuditorLoginDTO auditorLoginDTO) {
        String username = auditorLoginDTO.getUsername();
        String password = auditorLoginDTO.getPassword();

        Auditor auditor = auditorMapper.getByUsername(username);

        if (auditor == null) throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!(password.equals(auditor.getPassword()))) throw new RuntimeException(MessageConstant.PASSWORD_ERROR);

        if (auditor.getStatus() == UserStatusConstant.BANNED) throw new RuntimeException(MessageConstant.ACCOUNT_LOCKED);

        return auditor;
    }

    /**
     * 设置审核员状态
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        auditorMapper.setStatus(id, status);
    }

    /**
     * 修改密码
     * @param auditorPasswordDTO
     */
    @Override
    public void updatePassword(AuditorPasswordDTO auditorPasswordDTO) {
        Long auditorId = auditorPasswordDTO.getId();

        Auditor auditor = auditorMapper.getById(auditorId);
        if (auditor == null) throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);

        // 验证密码
        String oldPassword = DigestUtils.md5DigestAsHex(auditorPasswordDTO.getOldPassword().getBytes());
        if (!oldPassword.equals(auditor.getPassword())) throw new RuntimeException("原密码错误");

        // 修改密码
        auditor.setPassword(DigestUtils.md5DigestAsHex(auditorPasswordDTO.getNewPassword().getBytes()));
        auditor.setUpdateTime(LocalDateTime.now());

        // 更新管理员谢谢
        auditorMapper.update(auditor);
    }

    /**
     * 通过邮箱验证码重置密码
     * @param auditorResetPasswordDTO
     */
    @Override
    public void resetPassword(AuditorResetPasswordDTO auditorResetPasswordDTO) {
        String email = auditorResetPasswordDTO.getEmail();

        Auditor auditor = auditorMapper.getByEmail(email);
        if (auditor == null) throw new RuntimeException("该邮箱未啊绑定审核员账号");

        // 重置密码
        auditor.setPassword(DigestUtils.md5DigestAsHex(auditorResetPasswordDTO.getNewPassword().getBytes()));
        auditor.setUpdateTime(LocalDateTime.now());

        // 更新
        auditorMapper.update(auditor);
    }

}

package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.vo.AdminLoginVO;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.dto.AdminPasswordDTO;
import com.blog.pojo.dto.AdminResetPasswordDTO;
import com.blog.pojo.entity.Admin;
import com.blog.pojo.vo.AdminVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.AdminService;
import com.blog.service.VerificationCodeService;
import com.blog.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员管理
 *
 * @Author worlt
 * @Date 2025/5/18 下午7:46
 */
@RestController
@RequestMapping("/admin/admin")
@Slf4j
@Api(tags = "管理员相关接口")
public class AdminController {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AdminService adminService;
    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 登录
     *
     * @param adminLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("管理员登录：{}", adminLoginDTO);

        Admin admin = adminService.login(adminLoginDTO);

        // 登录成功后生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .avatar(admin.getAvatar())
                .role(admin.getRole())
                .token(token)
                .build();
        return Result.success(adminLoginVO);
    }


    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("管理员退出")
    public Result<String> logout() {
        log.info("管理员退出");
        return Result.success();
    }

    /**
     * 新增管理员
     * @param adminDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增管理员")
    public Result add(@RequestBody AdminDTO adminDTO) {
        log.info("新增管理员：{}", adminDTO);
        adminService.add(adminDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param adminPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("管理员分页查询")
    public Result<PageResult> page(AdminPageQueryDTO adminPageQueryDTO) {
        log.info("管理员分页查询：{}", adminPageQueryDTO);
        PageResult pageResult = adminService.pageQuery(adminPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用管理员
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("启用禁用管理员")
    public Result setStatus(@RequestParam("id") Long id,@PathVariable("status") Integer status) {
        log.info("启用禁用管理员：id：{}，status：{}", id, status);
        adminService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 编辑管理员信息
     * @param adminDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑管理员信息")
    public Result update(@RequestBody AdminDTO adminDTO) {
        log.info("编辑管理员：{}", adminDTO);
        adminService.update(adminDTO);
        return Result.success();
    }

    /**
     * 根据id删除管理员
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除管理员")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除管理员：{}", id);
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询管理员
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询管理员")
    public Result<AdminVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询管理员：{}", id);
        AdminVO adminVO = adminService.getById(id);
        return Result.success(adminVO);
    }
    
    /**
     * 修改密码
     * @param adminPasswordDTO
     * @return
     */
    @PutMapping("/updatePassword")
    @ApiOperation("修改管理员密码")
    public Result updatePassword(@RequestBody AdminPasswordDTO adminPasswordDTO) {
        log.info("修改管理员密码：{}", adminPasswordDTO);
        adminService.updatePassword(adminPasswordDTO);
        return Result.success();
    }
    
    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return
     */
    @PostMapping("/sendCode")
    @ApiOperation("发送邮箱验证码")
    public Result sendVerificationCode(@RequestParam String email) {
        log.info("发送邮箱验证码: {}", email);
        try {
            boolean success = verificationCodeService.sendVerificationCode(email);
            if (success) {
                return Result.success();
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("验证码发送过程中发生异常: {}", e.getMessage(), e);
            return Result.error("系统繁忙，请稍后重试");
        }
    }
    
    /**
     * 通过邮箱验证码重置密码
     * @param resetPasswordDTO
     * @return
     */
    @PostMapping("/resetPassword")
    @ApiOperation("通过邮箱验证码重置密码")
    public Result resetPassword(@RequestBody AdminResetPasswordDTO resetPasswordDTO) {
        log.info("通过邮箱验证码重置密码: {}", resetPasswordDTO);
        try {
            // 验证码校验
            boolean codeValid = verificationCodeService.verifyCode(
                    resetPasswordDTO.getEmail(), 
                    resetPasswordDTO.getCode());
            
            if (!codeValid) {
                return Result.error("验证码错误或已过期，请重试");
            }
            
            // 重置密码
            adminService.resetPassword(resetPasswordDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("重置密码过程中发生异常: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}

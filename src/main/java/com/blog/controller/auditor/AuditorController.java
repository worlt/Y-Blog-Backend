package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.*;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorLoginVO;
import com.blog.pojo.vo.AuditorVO;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.AuditorService;
import com.blog.service.CaptchaService;
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
 * @Author worlt
 * @Date 2025/6/2 下午12:53
 */
@RestController("auditorController")
@RequestMapping("/auditor/auditor")
@Api(tags = "审核员相关接口")
@Slf4j
public class AuditorController {

    @Autowired
    private AuditorService auditorService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 审核员登录
     * @param auditorLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("审核员登录")
    public Result<AuditorLoginVO> login(@RequestBody AuditorLoginDTO auditorLoginDTO) {
        log.info("审核员登录：{}", auditorLoginDTO);

        // 验证captchaToken（如果提供）
        if (auditorLoginDTO.getCaptchaToken() != null && !auditorLoginDTO.getCaptchaToken().isEmpty()) {
            boolean tokenValid = captchaService.verifyCaptchaToken(auditorLoginDTO.getCaptchaToken());
            if (!tokenValid) {
                log.warn("验证码token验证失败");
                return Result.error("验证码已过期，请重新验证");
            }
        }

        Auditor auditor = auditorService.login(auditorLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.AUDITOR_ID, auditor.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAuditorSecretKey(),
                jwtProperties.getAuditorTtl(),
                claims
        );

        AuditorLoginVO auditorLoginVO = AuditorLoginVO.builder()
                .id(auditor.getId())
                .username(auditor.getUsername())
                .name(auditor.getName())
                .avatar(auditor.getAvatar())
                .role(auditor.getRole())
                .token(token)
                .build();
        return Result.success(auditorLoginVO);
    }

    /**
     * 根据id查询审核员
     *
     * @return
     */
    @GetMapping("/getDetail")
    @ApiOperation("查询个人详情")
    public Result<AuditorVO> getDetail() {
        Long id = BaseContext.getCurrentId();
        log.info("根据id查询审核员：{}", id);
        AuditorVO auditorVO = auditorService.getById(id);
        return Result.success(auditorVO);
    }

    /**
     * 编辑审核员
     * @param auditorDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑审核员")
    public Result update(@RequestBody AuditorDTO auditorDTO) {
        Long id = BaseContext.getCurrentId();
        if (!id.equals(auditorDTO.getId())) throw new RuntimeException(MessageConstant.UNKNOWN_ERROR);
        auditorDTO.setId(id);
        log.info("编辑审核员：{}", auditorDTO);
        auditorService.update(auditorDTO);
        return Result.success();
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    public Result logout() {
        log.info("退出登录");
        return Result.success();
    }

    /**
     * 修改密码
     * @param auditorPasswordDTO
     * @return
     */
    @PutMapping("/updatePassword")
    @ApiOperation("修改审核员密码")
    public Result updatePassword(@RequestBody AuditorPasswordDTO auditorPasswordDTO) {
        log.info("修改管理员密码：{}", auditorPasswordDTO);
        auditorService.updatePassword(auditorPasswordDTO);
        return Result.success();
    }

    /**
     * 发送邮箱验证码
     * @param email
     * @return
     */
    @PostMapping("/sendCode")
    @ApiOperation("发送邮箱验证码")
    public Result sendVerificationCode(@RequestParam String email) {
        log.info("发送邮箱验证码：{}", email);
        try {
            boolean success = verificationCodeService.sendVerificationCode(email);
            if (success) {
                return Result.success();
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送验证码过程中发生异常：{}", e.getMessage(), e);
            return Result.error("系统繁忙，请稍后重试");
        }
    }

    /**
     * 通过邮箱验证码重置密码
     * @param auditorResetPasswordDTO
     * @return
     */
    @PostMapping("/resetPassword")
    @ApiOperation("通过邮箱验证码重置密码")
    public Result resetPassword(@RequestBody AuditorResetPasswordDTO auditorResetPasswordDTO) {
        log.info("通过邮箱验证码重置密码：{}", auditorResetPasswordDTO);
        try {
            // 检验验证码
            boolean codeValid = verificationCodeService.verifyCode(
                    auditorResetPasswordDTO.getEmail(),
                    auditorResetPasswordDTO.getCode());
            if (!codeValid) return Result.error("验证码错误或已过期，请重试");

            // 重置密码
            auditorService.resetPassword(auditorResetPasswordDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("重置密码异常：{}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

}

package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.*;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.UserLoginVO;
import com.blog.pojo.vo.UserVO;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.UserService;
import com.blog.service.VerificationCodeService;
import com.blog.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录注册
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:35
 */
@RestController
@RequestMapping("/user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 结果
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
     * 邮箱验证码注册
     * @param userEmailRegisterDTO 注册信息
     * @return 结果
     */
    @PostMapping("/register")
    @ApiOperation("邮箱验证码注册")
    public Result register(@RequestBody UserEmailRegisterDTO userEmailRegisterDTO) {
        log.info("用户注册: {}", userEmailRegisterDTO);
        
        try {
            // 验证码校验
            boolean codeValid = verificationCodeService.verifyCode(
                    userEmailRegisterDTO.getEmail(), 
                    userEmailRegisterDTO.getCode());
            
            if (!codeValid) {
                return Result.error("验证码错误或已过期");
            }
            
            // 注册用户
            userService.registerByEmail(userEmailRegisterDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("用户注册过程中发生异常: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 邮箱密码登录
     * @param userEmailLoginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login/password")
    @ApiOperation("邮箱密码登录")
    public Result<UserLoginVO> loginByPassword(@RequestBody UserEmailLoginDTO userEmailLoginDTO) {
        log.info("邮箱密码登录: {}", userEmailLoginDTO);
        
        try {
            User user = userService.loginByPassword(userEmailLoginDTO);
            
            // 登录成功后生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, user.getId());
            String token = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    claims
            );
            
            UserLoginVO userLoginVO = UserLoginVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .openid(user.getOpenid())
            .unionid(user.getUnionid())
            .role(user.getRole())
            .token(token)
            .build();
            
            return Result.success(userLoginVO);
        } catch (Exception e) {
            log.error("邮箱密码登录过程中发生异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 邮箱验证码登录
     * @param userEmailCodeLoginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login/code")
    @ApiOperation("邮箱验证码登录")
    public Result<UserLoginVO> loginByCode(@RequestBody UserEmailCodeLoginDTO userEmailCodeLoginDTO) {
        log.info("邮箱验证码登录: {}", userEmailCodeLoginDTO);
        
        try {
            // 验证码校验
            boolean codeValid = verificationCodeService.verifyCode(
                    userEmailCodeLoginDTO.getEmail(), 
                    userEmailCodeLoginDTO.getCode());
            
            if (!codeValid) {
                return Result.error("验证码错误或已过期");
            }
            
            User user = userService.loginByEmail(userEmailCodeLoginDTO.getEmail());
            
            // 登录成功后生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, user.getId());
            String token = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    claims
            );
            
            UserLoginVO userLoginVO = UserLoginVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .openid(user.getOpenid())
            .unionid(user.getUnionid())
            .role(user.getRole())
            .token(token)
            .build();
            
            return Result.success(userLoginVO);
        } catch (Exception e) {
            log.error("邮箱验证码登录过程中发生异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 找回密码
     * @param resetPasswordDTO 重置密码信息
     * @return 结果
     */
    @PostMapping("/resetPassword")
    @ApiOperation("找回密码")
    public Result resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.info("找回密码: {}", resetPasswordDTO);
        
        try {
            // 验证码校验
            boolean codeValid = verificationCodeService.verifyCode(
                    resetPasswordDTO.getEmail(), 
                    resetPasswordDTO.getCode());
            
            if (!codeValid) {
                return Result.error("验证码错误或已过期");
            }
            
            // 重置密码
            userService.resetPassword(resetPasswordDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("找回密码过程中发生异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 编辑用户
     * @param userDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑用户")
    public Result update(@RequestBody UserDTO userDTO) {
        log.info("编辑用户：{}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }

    /**
     * 根据id查询用户
     *
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询用户")
    public Result<UserVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询用户：{}", id);
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }
} 
package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.*;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.ShareVO;
import com.blog.pojo.vo.UserFollowVO;
import com.blog.pojo.vo.UserLoginVO;
import com.blog.pojo.vo.UserVO;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.CaptchaService;
import com.blog.service.ShareService;
import com.blog.service.UserFollowService;
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
import java.util.List;
import java.util.Map;

/**
 * 用户登录注册
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:35
 */
@RestController("userUserController")
@RequestMapping("/user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private UserFollowService userFollowService;
    
    @Autowired
    private ShareService shareService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CaptchaService captchaService;

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
            // 强制校验captchaToken（滑块验证码必须通过）
            if (userEmailLoginDTO.getCaptchaToken() == null || userEmailLoginDTO.getCaptchaToken().isEmpty()) {
                log.warn("登录请求缺少captchaToken，拒绝访问");
                return Result.error("请先完成滑块验证");
            }
            boolean tokenValid = captchaService.verifyCaptchaToken(userEmailLoginDTO.getCaptchaToken());
            if (!tokenValid) {
                log.warn("验证码token验证失败");
                return Result.error("验证码已过期，请重新验证");
            }

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
            // 强制校验captchaToken（滑块验证码必须通过）
            if (userEmailCodeLoginDTO.getCaptchaToken() == null || userEmailCodeLoginDTO.getCaptchaToken().isEmpty()) {
                log.warn("登录请求缺少captchaToken，拒绝访问");
                return Result.error("请先完成滑块验证");
            }
            boolean tokenValid = captchaService.verifyCaptchaToken(userEmailCodeLoginDTO.getCaptchaToken());
            if (!tokenValid) {
                log.warn("验证码token验证失败");
                return Result.error("验证码已过期，请重新验证");
            }

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
    
    /**
     * 关注/取消关注用户
     * @param userId 被关注用户id
     * @return 结果
     */
    @PostMapping("/follow/{id}")
    @ApiOperation("关注/取消关注用户")
    public Result follow(@PathVariable("id") Long userId) {
        log.info("关注/取消关注用户：{}", userId);
        Long currentUserId = BaseContext.getCurrentId();
        userFollowService.followUser(userId, currentUserId);
        return Result.success();
    }
    
    /**
     * 获取用户关注列表
     * @param userId 用户id，如果为空则获取当前用户的关注列表
     * @return 关注列表
     */
    @GetMapping("/follows")
    @ApiOperation("获取用户关注列表")
    public Result<List<UserFollowVO>> getFollows(@RequestParam(required = false) Long userId) {
        Long currentUserId = BaseContext.getCurrentId();
        Long targetUserId = userId != null ? userId : currentUserId;
        log.info("获取用户{}的关注列表", targetUserId);
        List<UserFollowVO> userFollowVOList = userFollowService.getFollowList(targetUserId, currentUserId);
        return Result.success(userFollowVOList);
    }
    
    /**
     * 获取用户粉丝列表
     * @param userId 用户id，如果为空则获取当前用户的粉丝列表
     * @return 粉丝列表
     */
    @GetMapping("/fans")
    @ApiOperation("获取用户粉丝列表")
    public Result<List<UserFollowVO>> getFans(@RequestParam(required = false) Long userId) {
        Long currentUserId = BaseContext.getCurrentId();
        Long targetUserId = userId != null ? userId : currentUserId;
        log.info("获取用户{}的粉丝列表", targetUserId);
        List<UserFollowVO> userFollowVOList = userFollowService.getFansList(targetUserId, currentUserId);
        return Result.success(userFollowVOList);
    }
    
    /**
     * 检查是否已关注用户
     * @param userId 被关注用户id
     * @return 是否已关注
     */
    @GetMapping("/isFollowed/{id}")
    @ApiOperation("检查是否已关注用户")
    public Result<Boolean> isFollowed(@PathVariable("id") Long userId) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("检查用户{}是否关注了用户{}", currentUserId, userId);
        boolean isFollowed = userFollowService.isFollowed(userId, currentUserId);
        return Result.success(isFollowed);
    }
    
    /**
     * 获取用户分享记录
     * @param userId 用户id，如果为空则获取当前登录用户的分享记录
     * @return 分享记录
     */
    @GetMapping("/shares")
    @ApiOperation("获取用户分享记录")
    public Result<List<ShareVO>> getUserShares(@RequestParam(required = false) Long userId) {
        log.info("获取用户分享记录: userId={}", userId);
        List<ShareVO> shares = shareService.getUserShares(userId);
        return Result.success(shares);
    }
    
    /**
     * 获取用户统计信息
     * @param userId 用户ID，如果不传则获取当前登录用户的统计信息
     * @return 用户统计信息
     */
    @GetMapping("/stats")
    @ApiOperation("获取用户统计信息")
    public Result<Map<String, Object>> getUserStats(@RequestParam(required = false) Long userId) {
        log.info("获取用户统计信息: userId={}", userId);
        
        // 如果没有传userId，则获取当前登录用户的统计信息
        if (userId == null) {
            userId = BaseContext.getCurrentId();
        }
        
        try {
            Map<String, Object> stats = userService.getUserStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计信息出错: {}", e.getMessage(), e);
            return Result.error("获取用户统计信息失败");
        }
    }
} 
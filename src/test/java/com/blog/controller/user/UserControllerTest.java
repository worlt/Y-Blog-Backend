package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.*;
import com.blog.properties.JwtProperties;
import com.blog.service.CaptchaService;
import com.blog.service.UserFollowService;
import com.blog.service.UserService;
import com.blog.service.VerificationCodeService;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端核心账户业务控制层测试
 * 包括注册、全量登录方式、密码找回、个人资料管理、
 * 用户统计及社交关注/粉丝管理流程。
 * 
 * @author worlt
 * @date 2025/5/18
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private UserFollowService userFollowService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtProperties jwtProperties;

    private User mockUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(100L);
        mockUser.setUsername("testUser");
        mockUser.setRole(2);

        String secret = "user_secret_123456789012345678901234567890";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        // 模拟用户查询绕过拦截器
        when(userMapper.getById(anyLong())).thenReturn(new User());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 100L);
        validToken = JwtUtil.createJWT(secret, 7200000L, claims);
        
        BaseContext.setCurrentId(100L);
    }

    /**
     * 测试用例: 正常邮箱验证码注册
     * 期待结果: 正常注册成功 (状态码 200, 业务代码 0)
     */
    @Test
    void register_Success() throws Exception {
        UserEmailRegisterDTO dto = new UserEmailRegisterDTO();
        dto.setEmail("test@test.com");
        dto.setCode("123456");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/user/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 密码登录流程
     * 期待结果: 登录成功 (状态码 200, 业务代码 0, 返回有效的 Token)
     */
    @Test
    void loginByPassword_Success() throws Exception {
        UserEmailLoginDTO dto = new UserEmailLoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setCaptchaToken("captcha-token");

        when(captchaService.verifyCaptchaToken(anyString())).thenReturn(true);
        when(userService.loginByPassword(any())).thenReturn(mockUser);

        mockMvc.perform(post("/user/user/login/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").exists());
    }

    /**
     * 测试用例: 找回并重置密码
     * 期待结果: 重置密码成功 (状态码 200, 业务代码 0)
     */
    @Test
    void resetPassword_Success() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("test@test.com");
        dto.setCode("123456");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/user/user/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取个人统计详情
     * 期待结果: 获取个人统计成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getUserStats_Success() throws Exception {
        when(userService.getUserStats(anyLong())).thenReturn(new HashMap<>());

        mockMvc.perform(get("/user/user/stats")
                .param("userId", "100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 社交互动 - 关注用户
     * 期待结果: 关注成功 (状态码 200, 业务代码 0)
     */
    @Test
    void followUser_Success() throws Exception {
        mockMvc.perform(post("/user/user/follow/200")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取粉丝列表
     * 期待结果: 获取成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getFans_Success() throws Exception {
        when(userFollowService.getFansList(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/user/user/fans")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 更新个人信息资料
     * 期待结果: 资料更新成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updateUserInfo_Success() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("NewName");

        mockMvc.perform(put("/user/user/update")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

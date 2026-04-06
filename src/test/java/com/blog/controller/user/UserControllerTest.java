package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.*;
import com.blog.properties.JwtProperties;
import com.blog.service.CaptchaService;
import com.blog.service.ShareService;
import com.blog.service.UserFollowService;
import com.blog.service.UserService;
import com.blog.service.VerificationCodeService;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端核心业务控制层测试
 * 覆盖登录、注册、找回密码、社交关注、统计信息获取等功能
 * 
 * @author worlt
 * @date 2025/5/18
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
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
    private ShareService shareService;

    @MockBean
    private JwtProperties jwtProperties;

    private User mockUser;
    private String token;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(100L);
        mockUser.setUsername("testUser");
        mockUser.setAvatar("default.jpg");
        mockUser.setRole(2); // 普通用户
        mockUser.setStatus(0); // 正常

        // Mock JwtProperties
        when(jwtProperties.getUserSecretKey()).thenReturn("12345678901234567890123456789012");
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        // 关键：拦截器 JwtTokenUserInterceptor 依赖此方法检查用户状态
        // 必须 Mock 以避免在测试环境下触发数据库连接
        when(userMapper.getById(anyLong())).thenReturn(new com.blog.pojo.entity.User());

        // 生成测试 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 100L);
        token = JwtUtil.createJWT("12345678901234567890123456789012", 7200000L, claims);
        
        // 设置上下文
        BaseContext.setCurrentId(100L);
    }

    /**
     * 验证JWT生成和解析链路
     */
    @Test
    void testJwtGenerateAndParse() {
        String secret = "abcdefghijklmnopqrstuvwxyz123456";
        long ttl = 3600000L;
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 999L);

        // 1. 生成
        String token = JwtUtil.createJWT(secret, ttl, claims);
        assertNotNull(token);

        // 2. 解析
        Claims parsedClaims = JwtUtil.parseJWT(secret, token);
        assertNotNull(parsedClaims);
        assertEquals(999L, Long.valueOf(parsedClaims.get(JwtClaimsConstant.USER_ID).toString()));
    }

    /**
     * 测试用例: 邮箱验证码注册
     */
    @Test
    void register_Success() throws Exception {
        UserEmailRegisterDTO dto = new UserEmailRegisterDTO();
        dto.setEmail("test@example.com");
        dto.setCode("123456");
        dto.setPassword("123456");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/user/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 正常邮箱密码登录
     */
    @Test
    void loginByPassword_Success() throws Exception {
        UserEmailLoginDTO dto = new UserEmailLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("123456");
        dto.setCaptchaToken("valid-token");

        when(captchaService.verifyCaptchaToken(anyString())).thenReturn(true);
        when(userService.loginByPassword(any())).thenReturn(mockUser);

        mockMvc.perform(post("/user/user/login/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("testUser"));
    }

    /**
     * 测试用例: 找回密码
     */
    @Test
    void resetPassword_Success() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("test@example.com");
        dto.setCode("123456");
        dto.setNewPassword("newpassword");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/user/user/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 关注/取消关注用户
     */
    @Test
    void follow_Success() throws Exception {
        mockMvc.perform(post("/user/user/follow/200")
                .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取用户统计信息
     */
    @Test
    void getUserStats_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("blogCount", 5);
        stats.put("fansCount", 10);
        stats.put("followCount", 20);

        when(userService.getUserStats(anyLong())).thenReturn(stats);

        mockMvc.perform(get("/user/user/stats")
                .param("userId", "100")
                .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.blogCount").value(5));
    }

    /**
     * 测试用例: 获取用户粉丝列表
     */
    @Test
    void getFans_Success() throws Exception {
        when(userFollowService.getFansList(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/user/fans")
                .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 滑块验证缺失
     */
    @Test
    void loginByPassword_MissingCaptcha() throws Exception {
        UserEmailLoginDTO dto = new UserEmailLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("123456");

        mockMvc.perform(post("/user/user/login/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.msg").value("请先完成滑块验证"));
    }
}

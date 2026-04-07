package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.dto.AdminResetPasswordDTO;
import com.blog.pojo.entity.Admin;
import com.blog.pojo.vo.AdminVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.AdminService;
import com.blog.service.CaptchaService;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端核心账户与系统维护控制层测试
 * 包括管理员登录、个人资料管理、密码重置及系统级管理员维护。
 * 
 * @author worlt
 * @date 2025/5/17
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private JwtProperties jwtProperties;

    private String validAdminToken;

    @BeforeEach
    void setUp() {
        String secret = "admin_secret_123456789012345678901234567890";
        when(jwtProperties.getAdminSecretKey()).thenReturn(secret);
        when(jwtProperties.getAdminTtl()).thenReturn(7200000L);
        when(jwtProperties.getAdminTokenName()).thenReturn("token");

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, 1L);
        validAdminToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 管理员登录成功流程
     * 期待结果: 登录成功 (状态码 200, 业务代码 0, 返回有效的 Token)
     */
    @Test
    void login_Success() throws Exception {
        AdminLoginDTO loginDTO = new AdminLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");

        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");

        when(adminService.login(any(AdminLoginDTO.class))).thenReturn(admin);

        mockMvc.perform(post("/admin/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").exists());
    }

    /**
     * 测试用例: 管理员退出登录
     * 期待结果: 退出成功 (状态码 200, 业务代码 0)
     */
    @Test
    void logout_Success() throws Exception {
        mockMvc.perform(post("/admin/admin/logout")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 管理员分页查询
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(adminService.pageQuery(any(AdminPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/admin/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据ID查询管理员详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(adminService.getById(anyLong())).thenReturn(new AdminVO());

        mockMvc.perform(get("/admin/admin/getById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 编辑管理员信息
     * 期待结果: 更新信息成功 (状态码 200, 业务代码 0)
     */
    @Test
    void update_Success() throws Exception {
        AdminDTO dto = new AdminDTO();
        dto.setId(1L);
        dto.setName("UpdatedAdmin");

        mockMvc.perform(put("/admin/admin/update")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 重置管理员密码 (基于邮箱验证码)
     * 期待结果: 重置密码成功 (状态码 200, 业务代码 0)
     */
    @Test
    void resetPassword_Success() throws Exception {
        AdminResetPasswordDTO dto = new AdminResetPasswordDTO();
        dto.setEmail("admin@test.com");
        dto.setCode("123456");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/admin/admin/resetPassword")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除管理员
     * 期待结果: 删除成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/admin/deleteById/2")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

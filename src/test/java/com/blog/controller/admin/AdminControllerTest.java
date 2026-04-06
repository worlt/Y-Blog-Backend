package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.entity.Admin;
import com.blog.properties.JwtProperties;
import com.blog.service.AdminService;
import com.blog.service.AuditorService;
import com.blog.service.CaptchaService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端核心业务接口测试
 * 覆盖登录、管理员维护、审核员维护等功能
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
    private AuditorService auditorService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private JwtProperties jwtProperties;

    private String validAdminToken;

    @BeforeEach
    void setUp() {
        String secret = "12345678901234567890123456789012";
        when(jwtProperties.getAdminSecretKey()).thenReturn(secret);
        when(jwtProperties.getAdminTtl()).thenReturn(7200000L);
        when(jwtProperties.getAdminTokenName()).thenReturn("token");

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, 1L);
        validAdminToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 管理员登录成功
     * @author worlt
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
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void addAdmin_Success() throws Exception {
        AdminDTO dto = new AdminDTO();
        dto.setUsername("newAdmin");
        dto.setName("New Admin");

        mockMvc.perform(post("/admin/admin/add")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void addAuditor_Success() throws Exception {
        mockMvc.perform(post("/admin/auditor/add")
                .header("token", validAdminToken)
                .param("username", "newAuditor")
                .param("name", "New Auditor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void setAuditorStatus_Success() throws Exception {
        mockMvc.perform(put("/admin/auditor/status/1")
                .header("token", validAdminToken)
                .param("id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

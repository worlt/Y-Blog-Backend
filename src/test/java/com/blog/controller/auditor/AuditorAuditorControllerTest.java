package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AuditorDTO;
import com.blog.pojo.dto.AuditorLoginDTO;
import com.blog.pojo.dto.AuditorPasswordDTO;
import com.blog.pojo.dto.AuditorResetPasswordDTO;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorVO;
import com.blog.properties.JwtProperties;
import com.blog.service.AuditorService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 审核员自我管理控制器测试
 * 覆盖登录、个人详情、更新信息、密码修改及重置等
 * 
 * @author worlt
 * @date 2025/6/2
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditorAuditorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditorService auditorService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private JwtProperties jwtProperties;

    private String validAuditorToken;

    @BeforeEach
    void setUp() {
        String secret = "auditor_secret_123456789012345678901234567890";
        when(jwtProperties.getAuditorSecretKey()).thenReturn(secret);
        when(jwtProperties.getAuditorTtl()).thenReturn(7200000L);
        when(jwtProperties.getAuditorTokenName()).thenReturn("token");

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.AUDITOR_ID, 2L);
        validAuditorToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 审核员登录
     * 期待结果: 登录成功 (状态码 200, 业务代码 0, 用户名为 auditor01)
     */
    @Test
    void login_Success() throws Exception {
        Auditor auditor = new Auditor();
        auditor.setId(2L);
        auditor.setUsername("auditor01");
        
        AuditorLoginDTO loginDTO = new AuditorLoginDTO();
        loginDTO.setUsername("auditor01");
        loginDTO.setPassword("123456");

        when(auditorService.login(any(AuditorLoginDTO.class))).thenReturn(auditor);

        mockMvc.perform(post("/auditor/auditor/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("auditor01"));
    }

    /**
     * 测试用例: 查询个人详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getDetail_Success() throws Exception {
        when(auditorService.getById(anyLong())).thenReturn(new AuditorVO());

        mockMvc.perform(get("/auditor/auditor/getDetail")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 编辑审核员
     * 期待结果: 更新信息成功 (状态码 200, 业务代码 0)
     */
    @Test
    void update_Success() throws Exception {
        AuditorDTO dto = new AuditorDTO();
        dto.setId(2L);
        dto.setName("新名称");

        mockMvc.perform(put("/auditor/auditor/update")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 退出登录
     * 期待结果: 退出成功 (状态码 200, 业务代码 0)
     */
    @Test
    void logout_Success() throws Exception {
        mockMvc.perform(post("/auditor/auditor/logout")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 修改审核员密码
     * 期待结果: 修改密码成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updatePassword_Success() throws Exception {
        AuditorPasswordDTO dto = new AuditorPasswordDTO();
        dto.setOldPassword("123456");
        dto.setNewPassword("654321");

        mockMvc.perform(put("/auditor/auditor/updatePassword")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 发送邮箱验证码
     * 期待结果: 发送验证码成功 (状态码 200, 业务代码 0)
     */
    @Test
    void sendCode_Success() throws Exception {
        when(verificationCodeService.sendVerificationCode(anyString())).thenReturn(true);

        mockMvc.perform(post("/auditor/auditor/sendCode")
                .header("token", validAuditorToken)
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 通过邮箱验证码重置密码
     * 期待结果: 重置密码成功 (状态码 200, 业务代码 0)
     */
    @Test
    void resetPassword_Success() throws Exception {
        AuditorResetPasswordDTO dto = new AuditorResetPasswordDTO();
        dto.setEmail("test@example.com");
        dto.setCode("1234");
        dto.setNewPassword("newpass");

        when(verificationCodeService.verifyCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/auditor/auditor/resetPassword")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

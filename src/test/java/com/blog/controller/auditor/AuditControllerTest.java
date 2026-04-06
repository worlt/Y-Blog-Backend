package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.BlogAddReportDTO;
import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.BlogAuditorDTO;
import com.blog.properties.JwtProperties;
import com.blog.service.*;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 审核控制器测试类
 * 覆盖博客申诉、审计、举报等审核相关功能的测试
 * 
 * @author worlt
 * @date 2025/5/20
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogReportService blogReportService;

    @MockBean
    private BlogAuditService blogAuditService;

    @MockBean
    private CommentReportService commentReportService;

    @MockBean
    private UserReportService userReportService;

    @MockBean
    private AppealService appealService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtProperties jwtProperties;

    private String validUserToken;
    private String validAuditorToken;

    @BeforeEach
    void setUp() {
        String secret = "12345678901234567890123456789012";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getAuditorSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getAuditorTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");
        when(jwtProperties.getAuditorTokenName()).thenReturn("token");

        // 普通用户Token
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put(JwtClaimsConstant.USER_ID, 1L);
        validUserToken = JwtUtil.createJWT(secret, 7200000L, userClaims);

        // 审核员Token
        Map<String, Object> auditorClaims = new HashMap<>();
        auditorClaims.put(JwtClaimsConstant.AUDITOR_ID, 100L);
        validAuditorToken = JwtUtil.createJWT(secret, 7200000L, auditorClaims);

        // 模拟用户查询，用于拦截器校验封禁状态
        // 关键：JwtTokenUserInterceptor 会调用 userMapper.getById()，必须 Mock 以防止连接真实数据库
        when(userMapper.getById(anyLong())).thenReturn(new User());
        
        BaseContext.setCurrentId(100L);
    }

    /**
     * 测试用例: 提交博客举报
     * 验证用户提交举报信息后返回成功状态码 0
     * @author worlt
     */
    @Test
    void submitReport_Success() throws Exception {
        BlogAddReportDTO addDTO = new BlogAddReportDTO();
        addDTO.setBlogId(10L);
        addDTO.setReportReason("散布谣言");

        mockMvc.perform(post("/user/report/blog")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * @author worlt
     */
    @Test
    void claimBlogAudit_Success() throws Exception {
        mockMvc.perform(put("/auditor/blogAudit/inProcess/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 管理员/审核员将博客设置为“审核中”状态
     * 占用审核任务，防止他人重复操作
     * @author worlt
     */
    @Test
    void handleBlogAudit_InProcess() throws Exception {
        BlogAuditorDTO dto = new BlogAuditorDTO();
        dto.setBlogId(1L);
        dto.setStatus(2);

        mockMvc.perform(put("/auditor/blogAudit/handle")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * @author worlt
     */
    @Test
    void claimBlogReport_Success() throws Exception {
        mockMvc.perform(put("/auditor/blogReport/inProcess/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 将举报设置为“处理中”
     * @author worlt
     */
    @Test
    void handleBlogReport_InProcess() throws Exception {
        BlogReportDTO dto = new BlogReportDTO();
        dto.setId(1L);
        dto.setStatus(2);

        mockMvc.perform(put("/auditor/blogReport/handle")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

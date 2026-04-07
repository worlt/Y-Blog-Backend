package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AuditorDTO;
import com.blog.pojo.dto.AuditorPageQueryDTO;
import com.blog.pojo.vo.AuditorVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.AuditorService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端审核员管理控制器测试
 * 覆盖审核员的分页查询、添加、删除、更新及状态设置
 * 
 * @author worlt
 * @date 2025/6/2
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuditorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditorService auditorService;

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
     * 测试用例: 分页查询审核员
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(auditorService.page(any(AuditorPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/auditor/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 添加审核员
     */
    @Test
    void addAuditor_Success() throws Exception {
        AuditorDTO dto = new AuditorDTO();
        dto.setUsername("test_auditor");
        dto.setName("测试审核员");

        mockMvc.perform(post("/admin/auditor/add")
                .header("token", validAdminToken)
                .param("username", "test_auditor")
                .param("name", "测试审核员"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除审核员
     */
    @Test
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/auditor/deleteById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 更新审核员信息
     */
    @Test
    void updateAuditor_Success() throws Exception {
        AuditorDTO dto = new AuditorDTO();
        dto.setId(1L);
        dto.setName("更新后的名称");

        mockMvc.perform(put("/admin/auditor/update")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据ID查询审核员
     */
    @Test
    void getById_Success() throws Exception {
        when(auditorService.getById(anyLong())).thenReturn(new AuditorVO());

        mockMvc.perform(get("/admin/auditor/getById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 设置审核员状态
     */
    @Test
    void setStatus_Success() throws Exception {
        mockMvc.perform(put("/admin/auditor/status/1")
                .header("token", validAdminToken)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

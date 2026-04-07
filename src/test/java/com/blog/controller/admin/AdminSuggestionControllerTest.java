package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.SuggestionService;
import com.blog.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端建议管理控制器测试
 * 覆盖建议列表查询、状态修改、建议删除
 * 
 * @author worlt
 * @date 2025/6/10
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuggestionService suggestionService;

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
     * 测试用例: 建议列表
     */
    @Test
    void list_Success() throws Exception {
        when(suggestionService.list(any(SuggestionPageQueryDTO.class), anyBoolean())).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/suggestion/list")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 修改建议状态
     */
    @Test
    void updateStatus_Success() throws Exception {
        mockMvc.perform(put("/admin/suggestion/status/1/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除建议
     */
    @Test
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/admin/suggestion/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

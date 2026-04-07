package com.blog.controller;

import com.blog.constant.JwtClaimsConstant;
import com.blog.properties.JwtProperties;
import com.blog.service.WebsiteVisitService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 网站访问统计控制器测试
 * 覆盖记录访问及后台统计查看接口
 * 
 * @author worlt
 * @date 2025/6/28
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebsiteVisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebsiteVisitService websiteVisitService;

    @MockBean
    private JwtProperties jwtProperties;

    private String adminToken;

    @BeforeEach
    void setUp() {
        String secret = "admin_secret_123456789012345678901234567890";
        when(jwtProperties.getAdminSecretKey()).thenReturn(secret);
        when(jwtProperties.getAdminTtl()).thenReturn(7200000L);
        when(jwtProperties.getAdminTokenName()).thenReturn("token");

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, 1L);
        adminToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 记录访问 (公开接口)
     * 期待结果: 记录成功 (状态码 200, 业务代码 0)
     */
    @Test
    void recordVisit_Success() throws Exception {
        mockMvc.perform(post("/visit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取统计数据 (Admin 接口)
     * 期待结果: 获取成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getStats_Success() throws Exception {
        when(websiteVisitService.getStats(anyInt())).thenReturn(new HashMap<>());

        mockMvc.perform(get("/admin/visit/stats")
                .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取最近访问列表 (Admin 接口)
     * 期待结果: 获取列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getVisitList_Success() throws Exception {
        when(websiteVisitService.getRecentVisits(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/visit/list")
                .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取今日用户分布统计 (Admin 接口)
     * 期待结果: 获取各端分布成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getTodayStats_Success() throws Exception {
        when(websiteVisitService.getTodayUserStats()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/admin/visit/today")
                .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

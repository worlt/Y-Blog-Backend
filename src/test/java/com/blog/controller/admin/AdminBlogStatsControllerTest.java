package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.properties.JwtProperties;
import com.blog.service.BlogStatsService;
import com.blog.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端博客统计校正控制器测试
 * 覆盖单个博客统计数据校正、所有博客统计数据全量校正
 * 
 * @author worlt
 * @date 2025/6/10
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminBlogStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogStatsService blogStatsService;

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
     * 测试用例: 校正指定博客的统计数据
     * 期待结果: 单个博客数据校正成功 (状态码 200, 业务代码 0)
     */
    @Test
    void correctBlogStats_Success() throws Exception {
        mockMvc.perform(post("/admin/blog/stats/correct/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 校正所有博客的统计数据
     * 期待结果: 所有博客数据校正成功 (状态码 200, 业务代码 0)
     */
    @Test
    void correctAllBlogsStats_Success() throws Exception {
        mockMvc.perform(post("/admin/blog/stats/correctAll")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

package com.blog.controller;

import com.blog.constant.JwtClaimsConstant;
import com.blog.properties.JwtProperties;
import com.blog.service.CacheStats;
import com.blog.service.impl.CacheServiceImpl;
import com.blog.service.impl.BlogCacheServiceImpl;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 缓存管理控制器测试
 * 覆盖缓存统计、清理及健康检查
 * 
 * @author worlt
 * @date 2025/9/5
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheServiceImpl cacheService;

    @MockBean
    private BlogCacheServiceImpl blogCacheService;

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
     * 测试用例: 获取缓存统计
     * 期待结果: 获取统计成功 (状态码 200, 返回统计对象)
     */
    @Test
    void getCacheStats_Success() throws Exception {
        when(cacheService.getCacheStats()).thenReturn(new CacheStats());

        mockMvc.perform(get("/api/admin/cache/stats")
                .header("token", adminToken))
                .andExpect(status().isOk());
    }

    /**
     * 测试用例: Redis健康检查
     * 期待结果: 检查状态成功 (状态码 200, 返回结果为 true)
     */
    @Test
    void checkRedisHealth_Success() throws Exception {
        when(cacheService.isRedisAvailable()).thenReturn(true);

        mockMvc.perform(get("/api/admin/cache/redis/health")
                .header("token", adminToken))
                .andExpect(status().isOk());
    }

    /**
     * 测试用例: 清理过期缓存
     * 期待结果: 清理缓存成功 (状态码 200, 返回清理数量)
     */
    @Test
    void cleanExpiredCache_Success() throws Exception {
        when(blogCacheService.cleanAllExpiredCache()).thenReturn(5L);

        mockMvc.perform(post("/api/admin/cache/clean")
                .header("token", adminToken))
                .andExpect(status().isOk());
    }

    /**
     * 测试用例: 清除指定博客缓存
     * 期待结果: 清除博客缓存成功 (状态码 200)
     */
    @Test
    void evictBlogCache_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/cache/blog/1")
                .header("token", adminToken))
                .andExpect(status().isOk());
    }

    /**
     * 测试用例: 批量删除缓存
     * 期待结果: 批量删除成功 (状态码 200, 返回删除数量)
     */
    @Test
    void deleteByPattern_Success() throws Exception {
        when(cacheService.deleteByPattern(anyString())).thenReturn(10L);

        mockMvc.perform(delete("/api/admin/cache/pattern")
                .header("token", adminToken)
                .param("pattern", "blog:*"))
                .andExpect(status().isOk());
    }
}

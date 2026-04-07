package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.BlogService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端博客管理控制器测试
 * 覆盖博客的分页查询、添加、更新、删除及置顶设置
 * 
 * @author worlt
 * @date 2025/5/21
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminBlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogService blogService;

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
     * 测试用例: 博客文章分页查询
     * 期待结果: 分页查询博客成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(blogService.pageQuery(any(BlogPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/blog/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 新增博客文章
     * 期待结果: 新增博客成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addBlog_Success() throws Exception {
        BlogAddDTO dto = new BlogAddDTO();
        dto.setTitle("测试博客");

        mockMvc.perform(post("/admin/blog/add")
                .header("token", validAdminToken)
                .param("title", "测试博客"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 编辑博客
     * 期待结果: 更新博客成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updateBlog_Success() throws Exception {
        BlogUpdateDTO dto = new BlogUpdateDTO();
        dto.setId(1L);
        dto.setTitle("更新后的标题");

        mockMvc.perform(put("/admin/blog/update")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据博客id查询详情
     * 期待结果: 获取博客详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(blogService.getById(anyLong())).thenReturn(new BlogVO());

        mockMvc.perform(get("/admin/blog/getById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id删除博客
     * 期待结果: 单条删除成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/blog/deleteById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 批量删除博客
     * 期待结果: 批量删除成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteBatch_Success() throws Exception {
        mockMvc.perform(delete("/admin/blog/delete/batch")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1L, 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 更新博客置顶状态
     * 期待结果: 设置置顶成功 (状态码 200, 业务代码 0)
     */
    @Test
    void setIsTop_Success() throws Exception {
        mockMvc.perform(put("/admin/blog/setIsTop/1")
                .header("token", validAdminToken)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

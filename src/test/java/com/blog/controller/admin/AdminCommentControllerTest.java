package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.vo.CommentVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.CommentService;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端评论管理控制器测试
 * 覆盖评论的分页查询、添加、删除及详情获取
 * 
 * @author worlt
 * @date 2025/5/29
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

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
     * 测试用例: 分页查询评论
     * 期待结果: 分页查询评论成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(commentService.page(any(CommentPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/comment/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 新增评论
     * 期待结果: 新增评论成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addComment_Success() throws Exception {
        mockMvc.perform(post("/admin/comment/add")
                .header("token", validAdminToken)
                .param("blogId", "1")
                .param("content", "测试评论内容"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id删除评论
     * 期待结果: 删除评论成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/comment/deleteById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据ID查询评论详情
     * 期待结果: 获取评论详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(commentService.getById(anyLong())).thenReturn(new CommentVO());

        mockMvc.perform(get("/admin/comment/getById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

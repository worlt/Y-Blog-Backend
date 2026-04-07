package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.CommentVO;
import com.blog.properties.JwtProperties;
import com.blog.service.CommentService;
import com.blog.service.DislikeService;
import com.blog.service.LikeService;
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
 * 用户端评论控制器测试
 * 覆盖评论查询、发表、删除及点赞点踩交互
 * 
 * @author worlt
 * @date 2025/6/8
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private DislikeService dislikeService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtProperties jwtProperties;

    private String validUserToken;

    @BeforeEach
    void setUp() {
        String secret = "user_secret_123456789012345678901234567890";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        User user = new User();
        user.setId(3L);
        when(userMapper.getById(anyLong())).thenReturn(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 3L);
        validUserToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 获取博客所有评论
     * 期待结果: 获取成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getByBlogId_Success() throws Exception {
        when(commentService.getVOByBlogId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/user/comment/getByBlogId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 添加评论
     * 期待结果: 添加成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addComment_Success() throws Exception {
        CommentAddDTO dto = new CommentAddDTO();
        dto.setBlogId(1L);
        dto.setContent("用户评论测试");

        mockMvc.perform(post("/user/comment/add")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除自己的评论
     * 期待结果: 删除成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteById_Success() throws Exception {
        when(commentService.getUserId(anyLong())).thenReturn(3L); // 匹配当前登录用户ID

        mockMvc.perform(delete("/user/comment/deleteById/1")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 点赞评论
     * 期待结果: 点赞成功 (状态码 200, 业务代码 0)
     */
    @Test
    void likeComment_Success() throws Exception {
        mockMvc.perform(post("/user/comment/like/1")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取我的评论
     * 期待结果: 获取我的评论列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getMyComments_Success() throws Exception {
        when(commentService.getByUserId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/user/comment/getMyComments")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

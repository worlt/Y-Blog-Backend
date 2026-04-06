package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.vo.CommentVO;
import com.blog.properties.JwtProperties;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import com.blog.service.CollectionService;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 互动中心控制层测试
 * 覆盖点赞、收藏、取消点赞、评论发表与删除等用户交互功能
 * 
 * @author worlt
 * @date 2025/5/21
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InteractionTest {

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
    private CollectionService collectionService;

    @MockBean
    private BlogService blogService;

    @MockBean
    private JwtProperties jwtProperties;
 
    @MockBean
    private UserMapper userMapper;

    private String validToken;

    @BeforeEach
    void setUp() {
        String secret = "12345678901234567890123456789012";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");
 
        // 模拟用户查询用于拦截器
        when(userMapper.getById(anyLong())).thenReturn(new User());

        // 生成测试Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 1L);
        validToken = JwtUtil.createJWT(secret, 7200000L, claims);
        
        BaseContext.setCurrentId(1L);
    }

    @Test
    void getByBlogId_Success() throws Exception {
        when(commentService.getVOByBlogId(anyLong())).thenReturn(Collections.singletonList(new CommentVO()));

        mockMvc.perform(get("/user/comment/getByBlogId/10")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void addComment_Success() throws Exception {
        CommentAddDTO dto = new CommentAddDTO();
        dto.setBlogId(10L);
        dto.setContent("这是一条测试评论");

        mockMvc.perform(post("/user/comment/add")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void likeComment_Success() throws Exception {
        mockMvc.perform(post("/user/comment/like/50")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void dislikeComment_Success() throws Exception {
        mockMvc.perform(post("/user/comment/dislike/50")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 收藏博客
     * 使用较大的 ID 验证大数据量兼容性
     * @author worlt
     */
    @Test
    void collectBlog_Success() throws Exception {
        mockMvc.perform(post("/user/blog/collect/1000000")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除自己的评论
     * @author worlt
     */
    @Test
    void deleteComment_Success() throws Exception {
        when(commentService.getUserId(anyLong())).thenReturn(1L);

        mockMvc.perform(delete("/user/comment/deleteById/50000")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

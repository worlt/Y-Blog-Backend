package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.BlogAddReportDTO;
import com.blog.pojo.dto.CommentAddReportDTO;
import com.blog.pojo.dto.UserAddReportDTO;
import com.blog.pojo.entity.User;
import com.blog.properties.JwtProperties;
import com.blog.service.BlogReportService;
import com.blog.service.CommentReportService;
import com.blog.service.UserReportService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端举报控制器测试
 * 覆盖博客、评论及用户的举报入口
 * 
 * @author worlt
 * @date 2025/6/10
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogReportService blogReportService;

    @MockBean
    private CommentReportService commentReportService;

    @MockBean
    private UserReportService userReportService;

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
     * 测试用例: 举报博客
     * 期待结果: 举报成功 (状态码 200, 业务代码 0)
     */
    @Test
    void reportBlog_Success() throws Exception {
        BlogAddReportDTO dto = new BlogAddReportDTO();
        dto.setBlogId(1L);
        dto.setReportReason("违规内容");

        mockMvc.perform(post("/user/report/blog")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 举报评论
     * 期待结果: 举报成功 (状态码 200, 业务代码 0)
     */
    @Test
    void reportComment_Success() throws Exception {
        CommentAddReportDTO dto = new CommentAddReportDTO();
        dto.setCommentId(1L);
        dto.setReportReason("言语辱骂");

        mockMvc.perform(post("/user/report/comment")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 举报用户
     * 期待结果: 举报成功 (状态码 200, 业务代码 0)
     */
    @Test
    void reportUser_Success() throws Exception {
        UserAddReportDTO dto = new UserAddReportDTO();
        dto.setReportedUserId(1L);
        dto.setReportReason("恶意骚扰");

        mockMvc.perform(post("/user/report/user")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

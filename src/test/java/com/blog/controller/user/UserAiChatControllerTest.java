package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.AiChatDTO;
import com.blog.pojo.dto.AiCompletionDTO;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.AiChatVO;
import com.blog.pojo.vo.AiCompletionVO;
import com.blog.properties.JwtProperties;
import com.blog.service.AiChatService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端 AI 对话控制器测试
 * 覆盖 AI 对话、补全及会话清除
 * 
 * @author worlt
 * @date 2025/6/23
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiChatService aiChatService;

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

        // 模拟拦截器中的用户查询
        User user = new User();
        user.setId(3L);
        user.setUsername("test_user");
        when(userMapper.getById(anyLong())).thenReturn(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 3L);
        validUserToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: AI 对话
     * 期待结果: 对话成功 (状态码 200, 业务代码 0)
     */
    @Test
    void chat_Success() throws Exception {
        AiChatDTO dto = new AiChatDTO();
        dto.setMessage("你好");

        when(aiChatService.chat(any(AiChatDTO.class))).thenReturn(new AiChatVO());

        mockMvc.perform(post("/user/ai/chat")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: AI 对话内容为空
     * 期待结果: 对话校验成功 (业务代码 -1, 拒绝空消息)
     */
    @Test
    void chat_EmptyMessage() throws Exception {
        AiChatDTO dto = new AiChatDTO();
        dto.setMessage("");

        mockMvc.perform(post("/user/ai/chat")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1));
    }

    /**
     * 测试用例: AI 补齐
     * 期待结果: 补齐成功 (状态码 200, 业务代码 0)
     */
    @Test
    void completion_Success() throws Exception {
        AiCompletionDTO dto = new AiCompletionDTO();
        dto.setContent("内容");

        when(aiChatService.completion(any(AiCompletionDTO.class))).thenReturn(new AiCompletionVO());

        mockMvc.perform(post("/user/ai/completion")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 清除会话历史
     * 期待结果: 清除历史成功 (状态码 200, 业务代码 0)
     */
    @Test
    void clearConversation_Success() throws Exception {
        mockMvc.perform(delete("/user/ai/chat/conv123")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.MessageService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 私信/消息控制层测试
 * 验证会话创建、列表查询、消息发送以及撤回等功能的正向流程
 * 
 * @author worlt
 * @date 2025/5/22
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

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

    /**
     * 测试用例: 创建私聊会话
     * @author worlt
     */
    @Test
    void createPrivateConversation_Success() throws Exception {
        when(messageService.createPrivateConversation(anyLong(), anyLong())).thenReturn(Result.success(100L));

        mockMvc.perform(post("/user/message/conversations/private/2")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(100L));
    }

    /**
     * 测试用例: 获取会话列表
     * @author worlt
     */
    @Test
    void getConversations_Success() throws Exception {
        when(messageService.getUserConversations(anyLong())).thenReturn(Result.success(new ArrayList<>()));

        mockMvc.perform(get("/user/message/getConversations")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void getConversationMessages_Success() throws Exception {
        when(messageService.getConversationMessages(anyLong(), anyLong())).thenReturn(Result.success(new ArrayList<>()));

        mockMvc.perform(get("/user/message/getConversationMessages/10")
                .header("token", validToken)
                .param("autoMarkRead", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        
        verify(messageService, atLeastOnce()).markConversationAsRead(anyLong(), anyLong());
    }

    /**
     * 测试用例: 发送私信
     * @author worlt
     */
    @Test
    void sendPrivateMessage_Success() throws Exception {
        when(messageService.sendPrivateMessage(anyLong(), anyLong(), anyString()))
                .thenReturn(Result.success());

        mockMvc.perform(post("/user/message/send/private/2")
                .header("token", validToken)
                .param("content", "Hello!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void markAllRead_Success() throws Exception {
        when(messageService.markAllConversationsAsRead(anyLong())).thenReturn(5);

        mockMvc.perform(post("/user/message/conversations/read-all")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("已将5条消息标记为已读"));
    }

    /**
     * 测试用例: 撤回消息
     * @author worlt
     */
    @Test
    void retractMessage_Success() throws Exception {
        mockMvc.perform(put("/user/message/retract/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

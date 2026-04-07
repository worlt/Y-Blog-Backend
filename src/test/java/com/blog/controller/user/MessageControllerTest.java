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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端私信与消息通知控制器测试
 * 覆盖会话创建、列表查询、消息发送、已读标记及撤回操作。
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
        String secret = "user_secret_123456789012345678901234567890";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        // 模拟用户查询绕过拦截器
        when(userMapper.getById(anyLong())).thenReturn(new User());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 1L);
        validToken = JwtUtil.createJWT(secret, 7200000L, claims);
        
        BaseContext.setCurrentId(1L);
    }

    /**
     * 测试用例: 创建私聊会话
     * 期待结果: 创建私聊成功 (状态码 200, 业务代码 0, 返回会话 ID 100)
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
     * 测试用例: 获取当前用户的会话列表
     * 期待结果: 获取会话列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getConversations_Success() throws Exception {
        when(messageService.getUserConversations(anyLong())).thenReturn(Result.success(new ArrayList<>()));

        mockMvc.perform(get("/user/message/getConversations")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取会话内的消息详情，并自动标记已读
     * 期待结果: 获取消息成功并标记已读 (状态码 200, 业务代码 0)
     */
    @Test
    void getConversationMessages_Success() throws Exception {
        when(messageService.getConversationMessages(anyLong(), anyLong())).thenReturn(Result.success(new ArrayList<>()));

        mockMvc.perform(get("/user/message/getConversationMessages/10")
                .header("token", validToken)
                .param("autoMarkRead", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        
        verify(messageService).markConversationAsRead(eq(10L), eq(1L));
    }

    /**
     * 测试用例: 发送私信
     * 期待结果: 发送私信成功 (状态码 200, 业务代码 0)
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

    /**
     * 测试用例: 标记全量消息为已读
     * 期待结果: 标记全部已读成功 (状态码 200, 业务代码 0, 返回标记数量)
     */
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
     * 测试用例: 撤回已发送的消息
     * 期待结果: 撤回消息成功 (状态码 200, 业务代码 0)
     */
    @Test
    void retractMessage_Success() throws Exception {
        mockMvc.perform(put("/user/message/retract/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

}

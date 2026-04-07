package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.SuggestionAddDTO;
import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.pojo.dto.SuggestionVoteDTO;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.SuggestionVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.SuggestionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端建议控制器测试
 * 覆盖提交建议、查看列表、查看详情及投票交互
 * 
 * @author worlt
 * @date 2025/6/10
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuggestionService suggestionService;

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
     * 测试用例: 新增建议
     * 期待结果: 提交成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addSuggestion_Success() throws Exception {
        SuggestionAddDTO dto = new SuggestionAddDTO();
        dto.setTitle("建议标题");
        dto.setContent("建议内容");

        mockMvc.perform(post("/user/suggestion")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 建议列表
     * 期待结果: 获取列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void list_Success() throws Exception {
        when(suggestionService.list(any(SuggestionPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/user/suggestion/list")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 建议详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(suggestionService.getById(anyLong())).thenReturn(new SuggestionVO());

        mockMvc.perform(get("/user/suggestion/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 建议投票
     * 期待结果: 投票成功 (状态码 200, 业务代码 0)
     */
    @Test
    void vote_Success() throws Exception {
        SuggestionVoteDTO dto = new SuggestionVoteDTO();
        dto.setSuggestionId(1L);
        dto.setType(1); // 1-赞成

        mockMvc.perform(post("/user/suggestion/vote")
                .header("token", validUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserPageQueryDTO;
import com.blog.pojo.entity.User;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.UserService;
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
 * 管理端用户管理控制器测试
 * 覆盖用户的分页查询、添加、更新、状态设置及删除
 * 
 * @author worlt
 * @date 2025/5/20
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
     * 测试用例: 用户分页查询
     * 期待结果: 分页查询用户成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(userService.pageQuery(any(UserPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/user/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 新增用户
     * 期待结果: 新增用户成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addUser_Success() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");

        mockMvc.perform(post("/admin/user/add")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 编辑用户信息
     * 期待结果: 编辑用户信息成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updateUser_Success() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("新昵称");

        mockMvc.perform(put("/admin/user/update")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 启用禁用用户
     * 期待结果: 启用/禁用用户成功 (状态码 200, 业务代码 0)
     */
    @Test
    void setStatus_Success() throws Exception {
        mockMvc.perform(put("/admin/user/status/1")
                .header("token", validAdminToken)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id删除用户
     * 期待结果: 根据 id 删除用户成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/user/deleteById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id查询用户
     * 期待结果: 根据 id 查询用户成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(userService.getById(anyLong())).thenReturn(new User());

        mockMvc.perform(get("/admin/user/getById/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

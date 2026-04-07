package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AppealDTO;
import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.vo.AppealVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.AppealService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 审核端申诉管理控制器测试
 * 覆盖分页查询、详情查看、申诉处理及状态变更
 * 
 * @author worlt
 * @date 2025/6/4
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditorAppealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppealService appealService;

    @MockBean
    private JwtProperties jwtProperties;

    private String validAuditorToken;

    @BeforeEach
    void setUp() {
        String secret = "auditor_secret_123456789012345678901234567890";
        when(jwtProperties.getAuditorSecretKey()).thenReturn(secret);
        when(jwtProperties.getAuditorTtl()).thenReturn(7200000L);
        when(jwtProperties.getAuditorTokenName()).thenReturn("token");

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.AUDITOR_ID, 2L);
        validAuditorToken = JwtUtil.createJWT(secret, 7200000L, claims);
    }

    /**
     * 测试用例: 分页查询申诉
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(appealService.pageQuery(any(AppealPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/auditor/appeal/page")
                .header("token", validAuditorToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id查询申诉详情
     * 期待结果: 查询详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(appealService.getById(anyLong())).thenReturn(new AppealVO());

        mockMvc.perform(get("/auditor/appeal/getById/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 处理申诉
     * 期待结果: 处理申诉成功 (状态码 200, 业务代码 0)
     */
    @Test
    void handle_Success() throws Exception {
        AppealDTO dto = new AppealDTO();
        dto.setId(1L);
        dto.setStatus(2); // 2-已解决

        mockMvc.perform(put("/auditor/appeal/handle")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 设置申诉为处理中
     * 期待结果: 设置处理中成功 (状态码 200, 业务代码 0)
     */
    @Test
    void inProcess_Success() throws Exception {
        mockMvc.perform(put("/auditor/appeal/inProcess/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

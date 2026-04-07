package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.BlogReportPageQueryDTO;
import com.blog.pojo.vo.BlogReportVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.BlogReportService;
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
 * 审核端博客举报控制器测试
 * 覆盖分页查询、详情展示及举报处理流程
 * 
 * @author worlt
 * @date 2025/6/2
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditorBlogReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogReportService blogReportService;

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
     * 测试用例: 分页查询博客举报
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(blogReportService.pageQuery(any(BlogReportPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/auditor/blogReport/page")
                .header("token", validAuditorToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id查询举报详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(blogReportService.getById(anyLong())).thenReturn(new BlogReportVO());

        mockMvc.perform(get("/auditor/blogReport/getById/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 标记为处理中
     * 期待结果: 状态切换成功 (状态码 200, 业务代码 0)
     */
    @Test
    void inProcess_Success() throws Exception {
        mockMvc.perform(put("/auditor/blogReport/inProcess/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 处理举报
     * 期待结果: 举报处理成功 (状态码 200, 业务代码 0)
     */
    @Test
    void handle_Success() throws Exception {
        BlogReportDTO dto = new BlogReportDTO();
        dto.setId(1L);
        dto.setStatus(3); // 3-已处理

        mockMvc.perform(put("/auditor/blogReport/handle")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

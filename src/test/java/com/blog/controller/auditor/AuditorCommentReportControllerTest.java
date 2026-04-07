package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.CommentReportDTO;
import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.vo.CommentReportVO;
import com.blog.pojo.vo.CommentVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.CommentReportService;
import com.blog.service.CommentService;
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
 * 审核端评论举报相关接口测试
 * 覆盖评论举报分页查询、详情获取、处理举报及状态更新
 * 
 * @author worlt
 * @date 2025/6/4
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditorCommentReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentReportService commentReportService;

    @MockBean
    private CommentService commentService;

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
     * 测试用例: 评论举报分页查询
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(commentReportService.pageQuery(any(CommentReportPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/auditor/commentReport/page")
                .header("token", validAuditorToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id查询评论举报详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(commentReportService.getById(anyLong())).thenReturn(new CommentReportVO());

        mockMvc.perform(get("/auditor/commentReport/getById/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 处理评论举报
     * 期待结果: 举报处理成功 (状态码 200, 业务代码 0)
     */
    @Test
    void handle_Success() throws Exception {
        CommentReportDTO dto = new CommentReportDTO();
        dto.setId(1L);
        dto.setReviewResult(1); // 假设1是处理通过

        mockMvc.perform(put("/auditor/commentReport/handle")
                .header("token", validAuditorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 标记为处理中
     * 期待结果: 状态切换成功 (状态码 200, 业务代码 0)
     */
    @Test
    void inProcess_Success() throws Exception {
        mockMvc.perform(put("/auditor/commentReport/inProcess/1")
                .header("token", validAuditorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

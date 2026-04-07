package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.ReviewRecordPageQueryDTO;
import com.blog.pojo.vo.ReviewRecordVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.ReviewRecordService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端审核记录控制器测试
 * 覆盖审核记录的分页查询及详情获取
 * 
 * @author worlt
 * @date 2025/6/4
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminReviewRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRecordService reviewRecordService;

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
     * 测试用例: 分页查询审核记录
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(reviewRecordService.pageQuery(any(ReviewRecordPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/admin/reviewRecord/page")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据id查询审核记录详情
     * 期待结果: 获取详情成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getById_Success() throws Exception {
        when(reviewRecordService.getById(anyLong())).thenReturn(new ReviewRecordVO());

        mockMvc.perform(get("/admin/reviewRecord/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

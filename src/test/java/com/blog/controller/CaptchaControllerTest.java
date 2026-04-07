package com.blog.controller;

import com.blog.pojo.dto.CaptchaVerifyDTO;
import com.blog.pojo.vo.CaptchaPuzzleVO;
import com.blog.pojo.vo.CaptchaVerifyVO;
import com.blog.properties.JwtProperties;
import com.blog.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 滑动验证码控制器测试
 * 覆盖拼图获取及验证功能
 * 
 * @author worlt
 * @date 2025/12/10
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private JwtProperties jwtProperties;

    /**
     * 测试用例: 获取验证码拼图
     * 期待结果: 获取拼图成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getPuzzle_Success() throws Exception {
        when(captchaService.generatePuzzle(anyString())).thenReturn(new CaptchaPuzzleVO());

        mockMvc.perform(get("/captcha/puzzle")
                .param("difficulty", "normal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 验证验证码拼图
     * 期待结果: 验证通过 (状态码 200, 业务代码 0, 返回有效的 captchaToken)
     */
    @Test
    void verifyPuzzle_Success() throws Exception {
        CaptchaVerifyDTO dto = new CaptchaVerifyDTO();
        dto.setPuzzleId("puzzle123");
        dto.setOffsetX(100);
        dto.setOffsetY(0);
        dto.setDifficulty("normal");

        CaptchaVerifyVO verifyVO = new CaptchaVerifyVO();
        verifyVO.setCaptchaToken("valid_token");
        when(captchaService.verifyPuzzle(anyString(), anyInt(), anyInt(), anyString())).thenReturn(verifyVO);

        mockMvc.perform(post("/captcha/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

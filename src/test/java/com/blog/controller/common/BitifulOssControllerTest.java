package com.blog.controller.common;

import com.blog.constant.JwtClaimsConstant;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.properties.JwtProperties;
import com.blog.service.BitifulOssService;
import com.blog.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 文件上传控制器测试
 * 覆盖图片/文件上传、预签名URL获取及文件删除
 * 
 * @author worlt
 * @date 2025/6/7
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BitifulOssControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BitifulOssService bitifulOssService;

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
     * 测试用例: 上传图片
     * 期待结果: 上传成功 (状态码 200, 业务代码 0, 返回图片 URL)
     */
    @Test
    void uploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());
        
        when(bitifulOssService.uploadImage(any())).thenReturn("http://oss/images/test.jpg");

        mockMvc.perform(multipart("/common/oss/image")
                .file(file)
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("http://oss/images/test.jpg"));
    }

    /**
     * 测试用例: 上传文件
     * 期待结果: 上传成功 (状态码 200, 业务代码 0, 返回文件 URL)
     */
    @Test
    void uploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test file content".getBytes());
        
        when(bitifulOssService.uploadFile(any())).thenReturn("http://oss/files/test.txt");

        mockMvc.perform(multipart("/common/oss/file")
                .file(file)
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("http://oss/files/test.txt"));
    }

    /**
     * 测试用例: 获取预签名URL
     * 期待结果: 生成成功 (状态码 200, 业务代码 0)
     */
    @Test
    void generatePresignedUrl_Success() throws Exception {
        when(bitifulOssService.generatePresignedUrl(anyString())).thenReturn("http://presigned-url");

        mockMvc.perform(get("/common/oss/presigned")
                .param("objectKey", "test.jpg")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取预签名URL (自定义有效期)
     * 期待结果: 生成成功 (状态码 200, 业务代码 0)
     */
    @Test
    void generatePresignedUrlWithCustomExpiration_Success() throws Exception {
        when(bitifulOssService.generatePresignedUrl(anyString(), anyLong())).thenReturn("http://custom-presigned-url");

        mockMvc.perform(get("/common/oss/presigned/custom")
                .param("objectKey", "test.jpg")
                .param("expiration", "3600")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除对象
     * 期待结果: 删除成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteObject_Success() throws Exception {
        when(bitifulOssService.deleteObject(anyString())).thenReturn(true);

        mockMvc.perform(delete("/common/oss/delete")
                .param("objectKey", "test.jpg")
                .header("token", validUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));
    }
}

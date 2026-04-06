package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.*;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 博客核心业务控制层测试
 * 包含分页查询、详情获取、添加博客、更新博客等功能的正向逻辑验证
 * 
 * @author worlt
 * @date 2025/5/19
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogService blogService;

    @MockBean
    private JwtProperties jwtProperties;
    
    @MockBean
    private UserMapper userMapper;
    
    @MockBean
    private LikeService likeService;

    @MockBean
    private DislikeService dislikeService;

    @MockBean
    private CollectionService collectionService;

    @MockBean
    private ShareService shareService;

    @MockBean
    private ViewService viewService;

    private String validToken;

    @BeforeEach
    void setUp() {
        String secret = "12345678901234567890123456789012";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        // 模拟用户查询
        when(userMapper.getById(anyLong())).thenReturn(new User());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 1L);
        validToken = JwtUtil.createJWT(secret, 7200000L, claims);
        
        BaseContext.setCurrentId(1L);
    }

    /**
     * 测试用例: 正常添加新博客
     * @author worlt
     */
    @Test
    void addBlog_Success() throws Exception {
        BlogAddDTO dto = new BlogAddDTO();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setCategoryId(1L);

        when(blogService.add(any(BlogAddDTO.class))).thenReturn(100L);

        mockMvc.perform(post("/user/blog/add")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(100L));
    }

    /**
     * 测试用例: 更新博客信息
     * @author worlt
     */
    @Test
    void updateBlog_Success() throws Exception {
        BlogUpdateDTO dto = new BlogUpdateDTO();
        dto.setId(100L);
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");

        when(blogService.getUserId(anyLong())).thenReturn(1L); // 模拟作者是当前用户

        mockMvc.perform(put("/user/blog/update")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除博客
     * @author worlt
     */
    @Test
    void deleteById_Success() throws Exception {
        when(blogService.getUserId(anyLong())).thenReturn(1L); // 模拟作者是当前用户

        mockMvc.perform(delete("/user/blog/deleteById/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void viewBlog_Success() throws Exception {
        BlogVO vo = new BlogVO();
        vo.setId(100L);
        vo.setTitle("View Test");

        when(blogService.viewBlogDetail(anyLong(), anyLong(), anyString())).thenReturn(vo);

        mockMvc.perform(get("/user/blog/viewBlog/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(100L));
    }

    @Test
    void pageQuery_Success() throws Exception {
        PageResult pageResult = new PageResult();
        pageResult.setTotal(1L);
        pageResult.setRecords(Collections.singletonList(new BlogVO()));

        when(blogService.pageQuery(any(BlogPageQueryDTO.class))).thenReturn(pageResult);

        mockMvc.perform(get("/user/blog/page")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void collectBlog_Success() throws Exception {
        mockMvc.perform(post("/user/blog/collect/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

package com.blog.controller.user;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.pojo.vo.ShareStatisticsVO;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端博客核心业务控制层测试
 * 覆盖博客发布、编辑、删除、分页查询、详情展示、
 * 社交互动（点赞、点踩、收藏、分享）及浏览历史管理。
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
        String secret = "user_secret_123456789012345678901234567890";
        when(jwtProperties.getUserSecretKey()).thenReturn(secret);
        when(jwtProperties.getUserTtl()).thenReturn(7200000L);
        when(jwtProperties.getUserTokenName()).thenReturn("token");

        // 模拟用户查询用于拦截器绕过
        when(userMapper.getById(anyLong())).thenReturn(new User());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 1L);
        validToken = JwtUtil.createJWT(secret, 7200000L, claims);
        
        BaseContext.setCurrentId(1L);
    }

    /**
     * 测试用例: 分页查询博客
     * 期待结果: 分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void pageQuery_Success() throws Exception {
        when(blogService.pageQuery(any(BlogPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/user/blog/page")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 正常添加新博客
     * 期待结果: 新增博客成功 (状态码 200, 业务代码 0, 返回博客 ID)
     */
    @Test
    void addBlog_Success() throws Exception {
        BlogAddDTO dto = new BlogAddDTO();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");

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
     * 期待结果: 更新博客成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updateBlog_Success() throws Exception {
        BlogUpdateDTO dto = new BlogUpdateDTO();
        dto.setId(100L);
        dto.setTitle("Updated Title");

        when(blogService.getUserId(anyLong())).thenReturn(1L);

        mockMvc.perform(put("/user/blog/update")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取我的博客列表
     * 期待结果: 获取我的博客列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getMyBlogs_Success() throws Exception {
        when(blogService.pageQuery(any())).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/user/blog/getMyBlogs")
                .header("token", validToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 社交互动 - 点赞
     * 期待结果: 点赞操作成功 (状态码 200, 业务代码 0)
     */
    @Test
    void like_Success() throws Exception {
        mockMvc.perform(post("/user/blog/like/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(likeService).likeBlog(eq(100L), eq(1L));
    }

    /**
     * 测试用例: 社交互动 - 收藏/取消收藏
     * 期待结果: 收藏操作成功 (状态码 200, 业务代码 0)
     */
    @Test
    void collect_Success() throws Exception {
        mockMvc.perform(post("/user/blog/collect/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(collectionService).collectBlog(eq(100L), eq(1L));
    }

    /**
     * 测试用例: 获取我的收藏列表 (分页)
     * 期待结果: 获取收藏列表成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getCollections_Success() throws Exception {
        when(collectionService.getCollectionPage(any())).thenReturn(new PageResult(0L, new ArrayList<>()));

        mockMvc.perform(get("/user/blog/getCollections")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 分享博客
     * 期待结果: 分享博客成功 (状态码 200, 业务代码 0)
     */
    @Test
    void shareBlog_Success() throws Exception {
        mockMvc.perform(post("/user/blog/shareBlog/100")
                .header("token", validToken)
                .param("platform", "wechat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(shareService).shareBlog(eq(100L), eq(1L), eq("wechat"));
    }

    /**
     * 测试用例: 查看博客详情并记录浏览量
     * 期待结果: 查看详情及记录阅读量成功 (状态码 200, 业务代码 0)
     */
    @Test
    void viewBlog_Success() throws Exception {
        when(blogService.viewBlogDetail(anyLong(), anyLong(), anyString())).thenReturn(new BlogVO());

        mockMvc.perform(get("/user/blog/viewBlog/100")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 管理浏览历史 - 清空历史
     * 期待结果: 清空历史成功 (状态码 200, 业务代码 0)
     */
    @Test
    void clearViewHistory_Success() throws Exception {
        when(viewService.clearViewHistory(anyLong())).thenReturn(5);

        mockMvc.perform(delete("/user/blog/viewHistory/clear")
                .header("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(5));
    }
}

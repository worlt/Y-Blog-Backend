package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.CategoryDTO;
import com.blog.pojo.dto.CategoryPageQueryDTO;
import com.blog.pojo.dto.TagDTO;
import com.blog.pojo.dto.TagPageQueryDTO;
import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.Tag;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.service.CategoryService;
import com.blog.service.TagService;
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
 * 管理端标签和分类控制器测试
 * 覆盖标签与分类的增删改查
 * 
 * @author worlt
 * @date 2025/6/16
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminTagAndCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @MockBean
    private CategoryService categoryService;

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

    // --- 标签测试 ---

    /**
     * 测试用例: 获取所有标签
     * 期待结果: 获取所有标签成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getAllTags_Success() throws Exception {
        when(tagService.listAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/admin/tag-category/getAllTags")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 标签分页查询
     * 期待结果: 标签分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void tagPage_Success() throws Exception {
        when(tagService.pageQuery(any(TagPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));
        mockMvc.perform(get("/admin/tag-category/tagPage")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 新增标签
     * 期待结果: 新增标签成功 (状态码 200, 业务代码 0)
     */
    @Test
    void addTag_Success() throws Exception {
        TagDTO dto = new TagDTO();
        dto.setName("新标签");
        mockMvc.perform(post("/admin/tag-category/addTag")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 删除标签
     * 期待结果: 删除标签成功 (状态码 200, 业务代码 0)
     */
    @Test
    void deleteTag_Success() throws Exception {
        mockMvc.perform(delete("/admin/tag-category/deleteTag/1")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // --- 分类测试 ---

    /**
     * 测试用例: 获取所有分类
     * 期待结果: 获取所有分类成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getAllCategories_Success() throws Exception {
        when(categoryService.listAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/admin/tag-category/getAllCategories")
                .header("token", validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 分类分页查询
     * 期待结果: 分类分页查询成功 (状态码 200, 业务代码 0)
     */
    @Test
    void categoryPage_Success() throws Exception {
        when(categoryService.pageQuery(any(CategoryPageQueryDTO.class))).thenReturn(new PageResult(0L, new ArrayList<>()));
        mockMvc.perform(get("/admin/tag-category/categoryPage")
                .header("token", validAdminToken)
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 更新分类
     * 期待结果: 更新分类成功 (状态码 200, 业务代码 0)
     */
    @Test
    void updateCategory_Success() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setName("修改分类");
        mockMvc.perform(put("/admin/tag-category/updateCategory")
                .header("token", validAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

package com.blog.controller.user;

import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.Tag;
import com.blog.properties.JwtProperties;
import com.blog.service.CategoryService;
import com.blog.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端标签分类控制器测试
 * 覆盖标签与分类的公开查询接口
 * 
 * @author worlt
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserTagAndCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtProperties jwtProperties;

    /**
     * 测试用例: 获取所有标签
     * 期待结果: 获取所有标签成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getAllTags_Success() throws Exception {
        when(tagService.listAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/user/tag-category/getAllTags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取所有分类
     * 期待结果: 获取所有分类成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getAllCategories_Success() throws Exception {
        when(categoryService.listAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/user/tag-category/getAllCategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 根据ID获取标签
     * 期待结果: 获取成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getTagById_Success() throws Exception {
        when(tagService.getById(anyLong())).thenReturn(new Tag());
        mockMvc.perform(get("/user/tag-category/getTagById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试用例: 获取热门标签
     * 期待结果: 获取热门标签成功 (状态码 200, 业务代码 0)
     */
    @Test
    void getHotTags_Success() throws Exception {
        when(tagService.getHotTags(anyInt())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/user/tag-category/getHotTags")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

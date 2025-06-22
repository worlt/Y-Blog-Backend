package com.blog.service;

import com.blog.pojo.dto.CategoryDTO;
import com.blog.pojo.dto.CategoryPageQueryDTO;
import com.blog.pojo.entity.Category;
import com.blog.result.PageResult;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface CategoryService {
    
    /**
     * 新增分类
     * @param categoryDTO 分类信息
     */
    void add(CategoryDTO categoryDTO);
    
    /**
     * 更新分类
     * @param categoryDTO 分类信息
     */
    void update(CategoryDTO categoryDTO);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteById(Long id);
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category getById(Long id);
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> listAll();

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    
    /**
     * 获取热门分类
     * @param limit 限制数量
     * @return 热门分类列表
     */
    List<Category> getHotCategories(Integer limit);
}
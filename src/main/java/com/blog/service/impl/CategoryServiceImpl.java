package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.pojo.dto.CategoryDTO;
import com.blog.pojo.dto.CategoryPageQueryDTO;
import com.blog.pojo.entity.Category;
import com.blog.result.PageResult;
import com.blog.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理服务实现类
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogMapper blogMapper;
    
    /**
     * 添加分类
     * 
     * @param categoryDTO 分类数据传输对象
     * @throws RuntimeException 当分类名已存在或添加失败时抛出
     */
    @Override
    @Transactional
    public void add(CategoryDTO categoryDTO) {
        // 1. 检查分类名称是否已存在
        Category existingCategory = categoryMapper.getByName(categoryDTO.getName());
        if (existingCategory != null) {
            throw new RuntimeException(MessageConstant.CATEGORY_ALREADY_EXISTS);
        }
        
        // 2. 创建分类
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
                
        int rows = categoryMapper.insert(category);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.CATEGORY_ADD_FAIL);
        }
        
        log.info("新增分类成功: {}", category.getName());
    }

    /**
     * 更新分类信息
     * 
     * @param categoryDTO 分类数据传输对象
     * @throws RuntimeException 当分类不存在、名称已被其他分类使用或更新失败时抛出
     */
    @Override
    @Transactional
    public void update(CategoryDTO categoryDTO) {
        // 1. 检查分类是否存在
        Category existingCategory = categoryMapper.getById(categoryDTO.getId());
        if (existingCategory == null) {
            throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        
        // 2. 检查是否有同名分类（非当前ID）
        Category categoryByName = categoryMapper.getByName(categoryDTO.getName());
        if (categoryByName != null && !categoryByName.getId().equals(categoryDTO.getId())) {
            throw new RuntimeException(MessageConstant.CATEGORY_ALREADY_EXISTS);
        }
        
        // 3. 更新分类
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        existingCategory.setUpdateTime(LocalDateTime.now());
        
        int rows = categoryMapper.update(existingCategory);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.UPDATEException);
        }
        
        log.info("更新分类成功: {}", existingCategory.getName());
    }

    /**
     * 删除分类
     * 删除前会检查是否有博客使用该分类，若有则不允许删除
     * 
     * @param id 分类ID
     * @throws RuntimeException 当分类不存在、被博客使用或删除失败时抛出
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 1. 检查分类是否存在
        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        
        // 2. 检查是否有博客使用该分类
        Integer count = blogMapper.countByCategoryId(id);
        if (count != null && count > 0) {
            throw new RuntimeException(MessageConstant.CATEGORY_IN_USE);
        }
        
        // 3. 删除分类
        int rows = categoryMapper.deleteById(id);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.CATEGORY_DELETE_FAIL);
        }
        
        log.info("删除分类成功: {}", category.getName());
    }

    /**
     * 根据ID获取分类详情
     * 
     * @param id 分类ID
     * @return 分类对象
     * @throws RuntimeException 当分类不存在时抛出
     */
    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    /**
     * 获取所有分类列表
     * 
     * @return 分类对象列表
     */
    @Override
    public List<Category> listAll() {
        return categoryMapper.listAll();
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.page(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    
    /**
     * 获取热门分类
     * 
     * @param limit 限制数量
     * @return 热门分类列表
     */
    @Override
    public List<Category> getHotCategories(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认限制为10个
        }
        return categoryMapper.getHotCategories(limit);
    }
} 
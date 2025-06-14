package com.blog.controller.admin;

import com.blog.pojo.dto.CategoryDTO;
import com.blog.pojo.dto.CategoryPageQueryDTO;
import com.blog.pojo.dto.TagDTO;
import com.blog.pojo.dto.TagPageQueryDTO;
import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.Tag;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.CategoryService;
import com.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员标签和分类控制器
 *
 * @Author worlt
 * @Date 2025/6/16 上午11:00
 */
@RestController
@RequestMapping("/admin/tag-category")
@Api(tags = "管理员标签和分类相关接口")
@Slf4j
@RequiredArgsConstructor
public class TagAndCategoryController {

    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping("/getAllTags")
    @ApiOperation("获取所有标签")
    public Result<List<Tag>> getAllTags() {
        log.info("获取所有标签");
        List<Tag> tags = tagService.listAll();
        return Result.success(tags);
    }
    
    /**
     * 标签分页查询
     * @param tagPageQueryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/tagPage")
    @ApiOperation("标签分页查询")
    public Result<PageResult> tagPage(TagPageQueryDTO tagPageQueryDTO) {
        log.info("标签分页查询：{}", tagPageQueryDTO);
        PageResult pageResult = tagService.pageQuery(tagPageQueryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签对象
     */
    @GetMapping("/getTagById/{id}")
    @ApiOperation("根据ID获取标签")
    public Result<Tag> getTagById(@PathVariable Long id) {
        log.info("根据ID获取标签：{}", id);
        Tag tag = tagService.getById(id);
        return Result.success(tag);
    }
    
    /**
     * 新增标签
     * @param tagDTO 标签信息
     * @return 结果
     */
    @PostMapping("/addTag")
    @ApiOperation("新增标签")
    public Result addTag(@RequestBody TagDTO tagDTO) {
        log.info("新增标签：{}", tagDTO);
        tagService.add(tagDTO);
        return Result.success();
    }
    
    /**
     * 更新标签
     * @param tagDTO 标签信息
     * @return 结果
     */
    @PutMapping("/updateTag")
    @ApiOperation("更新标签")
    public Result updateTag(@RequestBody TagDTO tagDTO) {
        log.info("更新标签：{}", tagDTO);
        tagService.update(tagDTO);
        return Result.success();
    }
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 结果
     */
    @DeleteMapping("/deleteTag/{id}")
    @ApiOperation("删除标签")
    public Result deleteTag(@PathVariable Long id) {
        log.info("删除标签：{}", id);
        tagService.deleteById(id);
        return Result.success();
    }
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    @GetMapping("/getAllCategories")
    @ApiOperation("获取所有分类")
    public Result<List<Category>> getAllCategories() {
        log.info("获取所有分类");
        List<Category> categories = categoryService.listAll();
        return Result.success(categories);
    }
    
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/categoryPage")
    @ApiOperation("分类分页查询")
    public Result<PageResult> categoryPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    @GetMapping("/getCategoryById/{id}")
    @ApiOperation("根据ID获取分类")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("根据ID获取分类：{}", id);
        Category category = categoryService.getById(id);
        return Result.success(category);
    }
    
    /**
     * 新增分类
     * @param categoryDTO 分类信息
     * @return 结果
     */
    @PostMapping("/addCategory")
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }
    
    /**
     * 更新分类
     * @param categoryDTO 分类信息
     * @return 结果
     */
    @PutMapping("/updateCategory")
    @ApiOperation("更新分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("更新分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 结果
     */
    @DeleteMapping("/deleteCategory/{id}")
    @ApiOperation("删除分类")
    public Result deleteCategory(@PathVariable Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }
} 
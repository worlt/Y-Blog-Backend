package com.blog.controller.user;

import com.blog.pojo.entity.Category;
import com.blog.pojo.entity.Tag;
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
 * 用户标签和分类控制器
 */
@RestController("userTagAndCategoryController")
@RequestMapping("/user/tag-category")
@Api(tags = "用户标签和分类相关接口")
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
        log.info("用户获取所有标签");
        List<Tag> tags = tagService.listAll();
        return Result.success(tags);
    }
    
    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签对象
     */
    @GetMapping("/getTagById/{id}")
    @ApiOperation("根据ID获取标签")
    public Result<Tag> getTagById(@PathVariable Long id) {
        log.info("用户根据ID获取标签：{}", id);
        Tag tag = tagService.getById(id);
        return Result.success(tag);
    }
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    @GetMapping("/getAllCategories")
    @ApiOperation("获取所有分类")
    public Result<List<Category>> getAllCategories() {
        log.info("用户获取所有分类");
        List<Category> categories = categoryService.listAll();
        return Result.success(categories);
    }
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    @GetMapping("/getCategoryById/{id}")
    @ApiOperation("根据ID获取分类")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("用户根据ID获取分类：{}", id);
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 获取热门标签
     * @param limit 限制数量，默认10个
     * @return 热门标签列表
     */
    @GetMapping("/getHotTags")
    @ApiOperation("获取热门标签")
    public Result<List<Tag>> getHotTags(@RequestParam(required = false) Integer limit) {
        log.info("用户获取热门标签，数量限制：{}", limit);
        List<Tag> hotTags = tagService.getHotTags(limit);
        return Result.success(hotTags);
    }

} 
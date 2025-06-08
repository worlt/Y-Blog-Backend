package com.blog.controller.user;

import com.blog.constant.MessageConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author worlt
 * @Date 2025/6/7 上午12:29
 */
@RestController("userBlogController")
@RequestMapping("/user/blog")
@Slf4j
@Api(tags = "博客相关接口")
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 分页查询博客
     *
     * @param blogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询博客")
    public Result<PageResult> page(BlogPageQueryDTO blogPageQueryDTO) {
        blogPageQueryDTO.setStatus(BlogStatusConstant.APPROVED);
        log.info("分页查询博客：{}", blogPageQueryDTO);
        PageResult pageResult = blogService.pageQuery(blogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增博客文章
     *
     * @param blogAddDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增博客文章")
    public Result add(@RequestBody BlogAddDTO blogAddDTO) {
        log.info("新增博客文章：{}", blogAddDTO);
        blogService.add(blogAddDTO);
        return Result.success();
    }

    /**
     * 根据id删除博客
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据博客id删除博客")
    public Result deleteById(@PathVariable("id") Long id) {
        Long userId = blogService.getUserId(id);
        if (!userId.equals(BaseContext.getCurrentId())) throw new RuntimeException(MessageConstant.DeleteException);
        log.info("根据id删除博客：{}", id);
        blogService.deleteById(id);
        return Result.success();
    }

    /**
     * 编辑博客
     *
     * @param blogUpdateDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑博客")
    public Result update(@RequestBody BlogUpdateDTO blogUpdateDTO) {
        blogUpdateDTO.setUserId(BaseContext.getCurrentId());
        Long userId = blogService.getUserId(blogUpdateDTO.getId());
        if (!userId.equals(BaseContext.getCurrentId())) throw new RuntimeException(MessageConstant.UPDATEException);
        blogService.update(blogUpdateDTO);
        return Result.success();
    }

    /**
     * 查询博客详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("查询博客详情")
    public Result<BlogVO> getById(@PathVariable("id") Long id) {
        log.info("查询博客详情：{}", id);
        return Result.success(blogService.getById(id));
    }

    /**
     * 获取我的博客列表
     *
     * @return
     */
    @GetMapping("/getMyBlog")
    @ApiOperation("获取我的博客")
    public Result<PageResult> getMyBlog() {
        Long usrId = BaseContext.getCurrentId();
        log.info("获取我的博客：{}", usrId);
        BlogPageQueryDTO blogPageQueryDTO = BlogPageQueryDTO.builder()
                .userId(usrId)
                .build();
        PageResult pageResult = blogService.pageQuery(blogPageQueryDTO);
        return Result.success(pageResult);
    }

}

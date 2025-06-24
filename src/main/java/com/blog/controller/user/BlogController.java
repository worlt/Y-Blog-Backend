package com.blog.controller.user;

import com.blog.constant.MessageConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.pojo.vo.ShareStatisticsVO;
import com.blog.pojo.vo.ShareVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogService;
import com.blog.service.LikeService;
import com.blog.service.DislikeService;
import com.blog.service.CollectionService;
import com.blog.service.ShareService;
import com.blog.service.ViewService;
import com.blog.pojo.vo.CollectionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private LikeService likeService;

    @Autowired
    private DislikeService dislikeService;

    @Autowired
    private CollectionService collectionService;
    
    @Autowired
    private ShareService shareService;

    @Autowired
    private ViewService viewService;

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
    @GetMapping("/getMyBlogs")
    @ApiOperation("获取我的博客")
    public Result<PageResult> getMyBlogs(BlogPageQueryDTO blogPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        log.info("获取我的博客：userId={}, params={}", userId, blogPageQueryDTO);
        
        // 设置当前用户ID，确保只查询当前用户的博客
        blogPageQueryDTO.setUserId(userId);
        
        PageResult pageResult = blogService.pageQuery(blogPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 点赞
     * @param id
     * @return
     */
    @PostMapping("/like/{id}")
    @ApiOperation("点赞")
    public Result like(@PathVariable("id") Long id) {
        log.info("点赞博客：{}", id);
        Long userId = BaseContext.getCurrentId();
        likeService.likeBlog(id, userId);
        return Result.success();
    }

    /**
     * 点踩
     * @param id
     * @return
     */
    @PostMapping("/dislike/{id}")
    @ApiOperation("点踩")
    public Result dislike(@PathVariable("id") Long id) {
        log.info("点踩博客：{}", id);
        Long userId = BaseContext.getCurrentId();
        dislikeService.dislikeBlog(id, userId);
        return Result.success();
    }

    /**
     * 收藏/取消收藏
     * @param id 博客id
     * @return 结果
     */
    @PostMapping("/collect/{id}")
    @ApiOperation("收藏/取消收藏")
    public Result collect(@PathVariable("id") Long id) {
        log.info("收藏/取消收藏博客：{}", id);
        Long userId = BaseContext.getCurrentId();
        collectionService.collectBlog(id, userId);
        return Result.success();
    }

    /**
     * 获取收藏列表(分页)
     * @param blogPageQueryDTO 分页参数
     * @return 收藏列表
     */
    @GetMapping("/getCollections")
    @ApiOperation("获取收藏列表(分页)")
    public Result<PageResult> getCollections(BlogPageQueryDTO blogPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的收藏列表，参数：{}", userId, blogPageQueryDTO);
        blogPageQueryDTO.setUserId(userId);
        PageResult pageResult = collectionService.getCollectionPage(blogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 检查是否已收藏
     * @param id 博客id
     * @return 是否已收藏
     */
    @GetMapping("/isCollected/{id}")
    @ApiOperation("检查是否已收藏")
    public Result<Boolean> isCollected(@PathVariable("id") Long id) {
        Long userId = BaseContext.getCurrentId();
        log.info("检查用户{}是否收藏了博客{}", userId, id);
        boolean isCollected = collectionService.isCollected(id, userId);
        return Result.success(isCollected);
    }
    
    /**
     * 分享博客
     * @param id 博客id
     * @param platform 分享平台
     * @return 结果
     */
    @PostMapping("/shareBlog/{id}")
    @ApiOperation("分享博客")
    public Result shareBlog(@PathVariable("id") Long id, @RequestParam String platform) {
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}分享博客{}到{}", userId, id, platform);
        shareService.shareBlog(id, userId, platform);
        return Result.success();
    }
    
    /**
     * 获取博客分享统计
     * @param id 博客id
     * @return 分享统计
     */
    @GetMapping("/getShareStatistics/{id}")
    @ApiOperation("获取博客分享统计")
    public Result<ShareStatisticsVO> getShareStatistics(@PathVariable("id") Long id) {
        log.info("获取博客{}的分享统计", id);
        ShareStatisticsVO statistics = shareService.getShareStatistics(id);
        return Result.success(statistics);
    }
    
    /**
     * 获取支持的分享平台
     * @return 平台列表
     */
    @GetMapping("/getSupportedPlatforms")
    @ApiOperation("获取支持的分享平台")
    public Result<List<String>> getSupportedPlatforms() {
        log.info("获取支持的分享平台");
        List<String> platforms = shareService.getSupportedPlatforms();
        return Result.success(platforms);
    }
    
    /**
     * 获取博客分享记录
     * @param id 博客id
     * @return 分享记录
     */
    @GetMapping("/getBlogShares/{id}")
    @ApiOperation("获取博客分享记录")
    public Result<List<ShareVO>> getBlogShares(@PathVariable("id") Long id) {
        log.info("获取博客{}的分享记录", id);
        List<ShareVO> shares = shareService.getBlogShares(id);
        return Result.success(shares);
    }

    /**
     * 查看博客详情并记录浏览量
     *
     * @param id 博客id
     * @return 博客详情
     */
    @GetMapping("/viewBlog/{id}")
    @ApiOperation("查看博客详情并记录浏览量")
    public Result<BlogVO> viewBlog(@PathVariable("id") Long id) {
        // 获取当前登录用户ID，未登录则不记录浏览量
        Long userId = BaseContext.getCurrentId();
        log.info("查看博客详情并记录浏览量：{}，用户：{}", id, userId);
        // 查看博客详情并记录浏览量
        BlogVO blogVO = blogService.viewBlogDetail(id, userId);
        return Result.success(blogVO);
    }

    /**
     * 获取用户浏览历史(分页)
     *
     * @param blogPageQueryDTO 分页参数
     * @return 浏览历史列表
     */
    @GetMapping("/viewHistory")
    @ApiOperation("获取用户浏览历史(分页)")
    public Result<PageResult> getViewHistory(BlogPageQueryDTO blogPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的浏览历史，参数：{}", userId, blogPageQueryDTO);
        blogPageQueryDTO.setUserId(userId);
        PageResult pageResult = viewService.getUserViewHistoryPage(blogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 检查用户是否已点赞博客
     * 
     * @param id 博客id
     * @return 是否已点赞
     */
    @GetMapping("/isLiked/{id}")
    @ApiOperation("检查用户是否已点赞博客")
    public Result<Boolean> isLiked(@PathVariable("id") Long id) {
        Long userId = BaseContext.getCurrentId();
        log.info("检查用户{}是否点赞了博客{}", userId, id);
        boolean isLiked = likeService.isLiked(id, userId);
        return Result.success(isLiked);
    }
    
    /**
     * 检查用户是否已点踩博客
     * 
     * @param id 博客id
     * @return 是否已点踩
     */
    @GetMapping("/isDisliked/{id}")
    @ApiOperation("检查用户是否已点踩博客")
    public Result<Boolean> isDisliked(@PathVariable("id") Long id) {
        Long userId = BaseContext.getCurrentId();
        log.info("检查用户{}是否点踩了博客{}", userId, id);
        boolean isDisliked = dislikeService.isDisliked(id, userId);
        return Result.success(isDisliked);
    }
    
    /**
     * 获取用户点赞博客列表(分页)
     * 
     * @param blogPageQueryDTO 分页参数
     * @return 点赞博客列表
     */
    @GetMapping("/getLikedBlogs")
    @ApiOperation("获取用户点赞博客列表(分页)")
    public Result<PageResult> getLikedBlogs(BlogPageQueryDTO blogPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的点赞博客列表，参数：{}", userId, blogPageQueryDTO);
        blogPageQueryDTO.setUserId(userId);
        PageResult pageResult = likeService.getUserLikedBlogsPage(blogPageQueryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 获取用户点踩博客列表(分页)
     * 
     * @param blogPageQueryDTO 分页参数
     * @return 点踩博客列表
     */
    @GetMapping("/getDislikedBlogs")
    @ApiOperation("获取用户点踩博客列表(分页)")
    public Result<PageResult> getDislikedBlogs(BlogPageQueryDTO blogPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户{}的点踩博客列表，参数：{}", userId, blogPageQueryDTO);
        blogPageQueryDTO.setUserId(userId);
        PageResult pageResult = dislikeService.getUserDislikedBlogsPage(blogPageQueryDTO);
        return Result.success(pageResult);
    }
}

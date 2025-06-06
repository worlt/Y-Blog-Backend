package com.blog.controller.common;

import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.result.Result;
import com.blog.service.BitifulOssService;
import com.blog.utils.FileValidationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @Author worlt
 * @Date 2025/6/7 下午8:30
 */
@RestController
@RequestMapping("/common/oss")
@Api(tags = "文件上传接口")
@Slf4j
public class BitifulOssController {

    @Autowired
    private BitifulOssService bitifulOssService;

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/image")
    @ApiOperation(value = "上传图片", notes = "上传图片到缤纷云OSS，会自动添加images/前缀")
    public Result<String> uploadImage(@ApiParam(value = "图片文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传图片: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        
        // 校验当前用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error(MessageConstant.USER_NOT_LOGIN);
        }
        
        // 验证文件
        if (!FileValidationUtil.validateImage(file)) {
            return Result.error("图片验证失败，请确保图片类型和大小正确");
        }
        
        // 验证文件名安全性
        if (!FileValidationUtil.isFilenameSafe(file.getOriginalFilename())) {
            return Result.error("文件名不安全，包含非法字符");
        }
        
        try {
            String objectKey = bitifulOssService.uploadImage(file);
            log.info("用户[{}]图片上传成功: {}", userId, objectKey);
            return Result.success(objectKey);
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping("/file")
    @ApiOperation(value = "上传文件", notes = "上传文件到缤纷云OSS，会自动添加files/前缀")
    public Result<String> uploadFile(@ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传文件: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        
        // 校验当前用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error(MessageConstant.USER_NOT_LOGIN);
        }
        
        // 验证文件
        if (!FileValidationUtil.validateFile(file)) {
            return Result.error("文件验证失败，请确保文件类型和大小正确");
        }
        
        // 验证文件名安全性
        if (!FileValidationUtil.isFilenameSafe(file.getOriginalFilename())) {
            return Result.error("文件名不安全，包含非法字符");
        }
        
        try {
            String objectKey = bitifulOssService.uploadFile(file);
            log.info("用户[{}]文件上传成功: {}", userId, objectKey);
            return Result.success(objectKey);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取预签名URL
     * @param objectKey 对象键
     * @return 预签名URL
     */
    @GetMapping("/presigned")
    @ApiOperation(value = "获取预签名URL", notes = "根据对象键获取预签名URL，默认有效期1小时")
    public Result<String> generatePresignedUrl(
            @ApiParam(value = "对象键", required = true) @RequestParam("objectKey") String objectKey) {
        log.info("获取预签名URL: {}", objectKey);
        try {
            String presignedUrl = bitifulOssService.generatePresignedUrl(objectKey);
            return Result.success(presignedUrl);
        } catch (Exception e) {
            log.error("获取预签名URL失败: {}", e.getMessage(), e);
            return Result.error("获取预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 获取预签名URL（自定义有效期）
     * @param objectKey 对象键
     * @param expiration 有效期（秒）
     * @return 预签名URL
     */
    @GetMapping("/presigned/custom")
    @ApiOperation(value = "获取自定义有效期的预签名URL", notes = "根据对象键和有效期获取预签名URL")
    public Result<String> generatePresignedUrlWithCustomExpiration(
            @ApiParam(value = "对象键", required = true) @RequestParam("objectKey") String objectKey,
            @ApiParam(value = "有效期（秒）", required = true) @RequestParam("expiration") Long expiration) {
        log.info("获取预签名URL: {}, 有效期: {}秒", objectKey, expiration);
        try {
            String presignedUrl = bitifulOssService.generatePresignedUrl(objectKey, expiration);
            return Result.success(presignedUrl);
        } catch (Exception e) {
            log.error("获取预签名URL失败: {}", e.getMessage(), e);
            return Result.error("获取预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象
     * @param objectKey 对象键
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除对象", notes = "根据对象键删除OSS中的对象")
    public Result<Boolean> deleteObject(
            @ApiParam(value = "对象键", required = true) @RequestParam("objectKey") String objectKey) {
        
        // 校验当前用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error(MessageConstant.USER_NOT_LOGIN);
        }
        
        log.info("用户[{}]删除对象: {}", userId, objectKey);
        try {
            boolean success = bitifulOssService.deleteObject(objectKey);
            if (success) {
                log.info("用户[{}]删除对象成功: {}", userId, objectKey);
                return Result.success(true);
            } else {
                return Result.error("删除对象失败");
            }
        } catch (Exception e) {
            log.error("删除对象失败: {}", e.getMessage(), e);
            return Result.error("删除对象失败: " + e.getMessage());
        }
    }
} 
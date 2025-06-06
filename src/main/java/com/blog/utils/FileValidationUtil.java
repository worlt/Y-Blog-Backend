package com.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

/**
 * 文件验证工具类
 *
 * @Author worlt
 * @Date 2025/6/8 下午2:40
 */
@Slf4j
public class FileValidationUtil {

    /**
     * 允许上传的图片类型列表
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/svg+xml", "image/bmp"
    );

    /**
     * 允许上传的文件类型列表
     */
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // docx
            "application/vnd.ms-excel", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
            "application/vnd.ms-powerpoint", 
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // pptx
            "text/plain", "application/zip", "application/x-rar-compressed",
            "application/x-7z-compressed", "application/x-tar"
    );

    /**
     * 图片最大尺寸（10MB）
     */
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    /**
     * 文件最大尺寸（50MB）
     */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 验证图片
     * @param file 图片文件
     * @return 验证通过返回true，否则返回false
     */
    public static boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("图片为空");
            return false;
        }

        // 验证文件大小
        if (file.getSize() > MAX_IMAGE_SIZE) {
            log.warn("图片大小超过限制: {}B > {}B", file.getSize(), MAX_IMAGE_SIZE);
            return false;
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            log.warn("不支持的图片类型: {}", contentType);
            return false;
        }

        return true;
    }

    /**
     * 验证文件
     * @param file 文件
     * @return 验证通过返回true，否则返回false
     */
    public static boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("文件为空");
            return false;
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("文件大小超过限制: {}B > {}B", file.getSize(), MAX_FILE_SIZE);
            return false;
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            log.warn("不支持的文件类型: {}", contentType);
            return false;
        }

        return true;
    }

    /**
     * 验证文件名是否安全
     * @param filename 文件名
     * @return 安全返回true，否则返回false
     */
    public static boolean isFilenameSafe(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        // 检查是否包含路径遍历攻击
        if (filename.contains("../") || filename.contains("..\\")) {
            log.warn("文件名包含路径遍历攻击: {}", filename);
            return false;
        }

        // 检查文件名是否包含非法字符
        String regex = "[A-Za-z0-9._\\-\\s\\u4e00-\\u9fa5]+";
        boolean isMatch = filename.matches(regex);
        
        if (!isMatch) {
            log.warn("文件名包含非法字符: {}", filename);
        }
        
        return isMatch;
    }
} 
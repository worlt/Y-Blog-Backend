package com.blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 缤纷云OSS服务接口
 *
 * @Author worlt
 * @Date 2025/6/7 下午8:10
 */
public interface BitifulOssService {

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file);

    /**
     * 上传文件
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 通过输入流上传图片
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentType 内容类型
     * @return 图片访问URL
     */
    String uploadImageFromStream(InputStream inputStream, String originalFilename, String contentType);
    
    /**
     * 通过输入流上传文件
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadFileFromStream(InputStream inputStream, String originalFilename, String contentType);

    /**
     * 获取预签名URL
     * @param objectKey 对象键
     * @return 预签名URL
     */
    String generatePresignedUrl(String objectKey);
    
    /**
     * 获取预签名URL
     * @param objectKey 对象键
     * @param expiration 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUrl(String objectKey, Long expiration);

    /**
     * 删除对象
     * @param objectKey 对象键
     * @return 是否删除成功
     */
    boolean deleteObject(String objectKey);
    
} 
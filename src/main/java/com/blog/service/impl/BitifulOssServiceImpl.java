package com.blog.service.impl;

import com.blog.config.BitifulOssConfig;
import com.blog.constant.BitifulOssConstant;
import com.blog.service.BitifulOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * 缤纷云OSS服务实现
 *
 * @Author worlt
 * @Date 2025/6/7 下午8:15
 */
@Service
@Slf4j
public class BitifulOssServiceImpl implements BitifulOssService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Autowired
    private BitifulOssConfig bitifulOssConfig;
    
    // 存储桶名称和前缀路径
    private String bucketName;
    private String prefixPath = "";
    private String domainUrl;
    
    /**
     * 初始化方法，解析存储桶配置
     */
    @PostConstruct
    public void init() {
        String bucketConfig = bitifulOssConfig.getBucketName();
        
        // 初始化域名URL
        this.domainUrl = bitifulOssConfig.getDomainUrl();
        if (this.domainUrl != null && !this.domainUrl.endsWith("/")) {
            this.domainUrl = this.domainUrl + "/";
        }
        
        // 检查是否包含路径分隔符
        if (bucketConfig.contains("/")) {
            int slashIndex = bucketConfig.indexOf("/");
            bucketName = bucketConfig.substring(0, slashIndex);
            prefixPath = bucketConfig.substring(slashIndex + 1);
            
            // 确保前缀路径以/结尾
            if (!prefixPath.isEmpty() && !prefixPath.endsWith("/")) {
                prefixPath = prefixPath + "/";
            }
        } else {
            // 没有子目录的情况
            bucketName = bucketConfig;
        }
        
        log.info("缤纷云OSS服务初始化完成: 存储桶={}, 前缀路径={}, 域名URL={}", bucketName, prefixPath, domainUrl);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            return uploadToS3(file.getInputStream(), file.getOriginalFilename(), 
                    file.getContentType(), file.getSize(), BitifulOssConstant.IMAGE_PATH_PREFIX);
        } catch (IOException e) {
            log.error("上传图片失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传图片失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            return uploadToS3(file.getInputStream(), file.getOriginalFilename(), 
                    file.getContentType(), file.getSize(), BitifulOssConstant.FILE_PATH_PREFIX);
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadImageFromStream(InputStream inputStream, String originalFilename, String contentType) {
        try {
            long contentLength = inputStream.available();
            return uploadToS3(inputStream, originalFilename, contentType, contentLength, BitifulOssConstant.IMAGE_PATH_PREFIX);
        } catch (IOException e) {
            log.error("上传图片失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传图片失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadFileFromStream(InputStream inputStream, String originalFilename, String contentType) {
        try {
            long contentLength = inputStream.available();
            return uploadToS3(inputStream, originalFilename, contentType, contentLength, BitifulOssConstant.FILE_PATH_PREFIX);
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String objectKey) {
        return generatePresignedUrl(objectKey, BitifulOssConstant.PRESIGNED_URL_EXPIRATION);
    }

    @Override
    public String generatePresignedUrl(String objectKey, Long expiration) {
        try {
            log.info("生成预签名URL: objectKey={}, 过期时间={}秒", objectKey, expiration);
            
            // 创建获取对象请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefixPath.isEmpty() ? objectKey : prefixPath + objectKey)
                    .build();

            // 创建预签名请求
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // 生成预签名URL
            String url = s3Presigner.presignGetObject(presignRequest).url().toString();
            log.info("预签名URL生成成功: objectKey={}, URL长度={}", objectKey, url.length());
            return url;
        } catch (S3Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成预签名URL失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteObject(String objectKey) {
        try {
            log.info("删除对象: objectKey={}", objectKey);
            String fullKey = prefixPath.isEmpty() ? objectKey : prefixPath + objectKey;
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullKey)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
            log.info("删除对象成功: objectKey={}", objectKey);
            return true;
        } catch (S3Exception e) {
            log.error("删除对象失败: {}, 错误: {}", objectKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 上传文件到S3
     *
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentType 内容类型
     * @param contentLength 内容长度
     * @param prefix 路径前缀
     * @return 对象键
     */
    private String uploadToS3(InputStream inputStream, String originalFilename, String contentType,
                             long contentLength, String prefix) {
        
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = generateUniqueFileName() + fileExtension;
        // 添加类型前缀和前缀路径
        String objectKey = prefix + uniqueFileName;
        String fullKey = prefixPath.isEmpty() ? objectKey : prefixPath + objectKey;
        
        log.info("准备上传文件: 原始文件名={}, 大小={}字节, 类型={}, 目标路径={}", 
                originalFilename, contentLength, contentType, fullKey);
        
        try {
            // 如果未指定contentType，尝试根据扩展名推断
            String finalContentType = contentType;
            if (finalContentType == null || finalContentType.equals("application/octet-stream")) {
                finalContentType = getMimeTypeFromExtension(fileExtension);
                log.info("根据扩展名推断ContentType: 扩展名={}, ContentType={}", fileExtension, finalContentType);
            }
            
            // 构建上传请求
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullKey)
                    .contentType(finalContentType)
                    .build();
            
            // 执行上传
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            log.info("上传成功: 文件名={}, 路径={}", originalFilename, fullKey);
            
            // 返回带有域名的完整URL，需要包含前缀路径
            String pathForUrl = prefixPath.isEmpty() ? objectKey : prefixPath + objectKey;
            String urlWithDomain = domainUrl != null ? domainUrl + pathForUrl : pathForUrl;
            log.info("返回完整URL: {}", urlWithDomain);
            
            return urlWithDomain;
        } catch (SdkException e) {
            log.error("上传失败: {}, 错误: {}", originalFilename, e.getMessage(), e);
            throw new RuntimeException("上传失败: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("关闭输入流失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
    
    /**
     * 生成唯一文件名
     *
     * @return 唯一文件名
     */
    private String generateUniqueFileName() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 根据文件扩展名获取MIME类型
     * @param extension 文件扩展名（包括点，如 ".jpg"）
     * @return MIME类型
     */
    private String getMimeTypeFromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "application/octet-stream";
        }
        
        String ext = extension.toLowerCase();
        switch (ext) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".txt":
                return "text/plain";
            case ".mp4":
                return "video/mp4";
            case ".mp3":
                return "audio/mpeg";
            default:
                return "application/octet-stream";
        }
    }
} 
package com.blog.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * 缤纷云OSS配置
 *
 * @Author worlt
 * @Date 2025/6/7 下午8:00
 */
@Configuration
@Slf4j
@Data
public class BitifulOssConfig {

    @Value("${blog.bitiful.endpoint}")
    private String endpoint;

    @Value("${blog.bitiful.region}")
    private String region;

    @Value("${blog.bitiful.access-key}")
    private String accessKey;

    @Value("${blog.bitiful.secret-key}")
    private String secretKey;

    @Value("${blog.bitiful.bucket-name}")
    private String bucketName;

    /**
     * 创建S3客户端
     */
    @Bean
    public S3Client s3Client() {
        log.info("初始化S3客户端: endpoint={}, region={}", endpoint, region);
        
        // 创建S3配置，启用路径样式访问
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .build();
        
        // 创建并返回S3客户端
        S3Client client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(s3Configuration)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
        
        log.info("S3客户端初始化完成");
        return client;
    }

    /**
     * 创建S3预签名客户端
     */
    @Bean
    public S3Presigner s3Presigner() {
        log.info("初始化S3预签名客户端: endpoint={}, region={}", endpoint, region);
        
        // 为预签名客户端创建配置，启用路径样式访问
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
        
        // 创建并返回S3预签名客户端
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(s3Configuration)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
        
        log.info("S3预签名客户端初始化完成");
        return presigner;
    }
} 
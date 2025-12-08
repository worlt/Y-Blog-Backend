package com.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类 - 升级版本修复安全漏洞
 * 
 * @author Worlt
 * @date 2025/6/17
 */
@Slf4j
public class JwtUtil {

    /**
     * 生成JWT令牌
     * 使用HS256算法，密钥使用HMAC-SHA安全密钥
     *
     * @param secretKey JWT密钥
     * @param ttlMillis JWT过期时间（毫秒）
     * @param claims    设置的信息
     * @return JWT令牌字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        try {
            // 创建安全密钥
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            
            // 生成JWT的时间
            long expMillis = System.currentTimeMillis() + ttlMillis;
            Date exp = new Date(expMillis);

            // 设置JWT的body
            JwtBuilder builder = Jwts.builder()
                    // 设置自定义声明
                    .claims(claims)
                    // 设置签名使用的签名算法和密钥
                    .signWith(key)
                    // 设置过期时间
                    .expiration(exp)
                    // 设置签发时间
                    .issuedAt(new Date());

            String jwt = builder.compact();
            log.debug("成功生成JWT令牌，过期时间：{}", exp);
            return jwt;
            
        } catch (Exception e) {
            log.error("生成JWT令牌失败：{}", e.getMessage());
            throw new RuntimeException("JWT令牌生成失败", e);
        }
    }

    /**
     * 解析JWT令牌
     *
     * @param secretKey JWT密钥，此密钥必须保存在服务端，不能暴露
     * @param token     加密后的令牌
     * @return Claims对象
     */
    public static Claims parseJWT(String secretKey, String token) {
        try {
            // 创建安全密钥
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            
            // 解析JWT
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
                    
            log.debug("成功解析JWT令牌");
            return claims;
            
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT令牌已过期：{}", e.getMessage());
            throw new RuntimeException("JWT令牌已过期", e);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("JWT签名验证失败：{}", e.getMessage());
            throw new RuntimeException("JWT签名验证失败", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT令牌格式错误：{}", e.getMessage());
            throw new RuntimeException("JWT令牌格式错误", e);
        } catch (Exception e) {
            log.error("解析JWT令牌失败：{}", e.getMessage());
            throw new RuntimeException("JWT令牌解析失败", e);
        }
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param secretKey JWT密钥
     * @param token     JWT令牌
     * @return 是否有效
     */
    public static boolean validateJWT(String secretKey, String token) {
        try {
            parseJWT(secretKey, token);
            return true;
        } catch (Exception e) {
            log.warn("JWT令牌验证失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从JWT中提取用户ID
     *
     * @param secretKey JWT密钥
     * @param token     JWT令牌
     * @param userIdKey 用户ID在claims中的键名
     * @return 用户ID
     */
    public static Long extractUserId(String secretKey, String token, String userIdKey) {
        try {
            Claims claims = parseJWT(secretKey, token);
            Object userIdObj = claims.get(userIdKey);
            if (userIdObj != null) {
                return Long.valueOf(userIdObj.toString());
            }
            log.warn("JWT令牌中未找到用户ID，键名：{}", userIdKey);
            return null;
        } catch (Exception e) {
            log.error("从JWT中提取用户ID失败：{}", e.getMessage());
            return null;
        }
    }
}

package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享表
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Share implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long blogId;

    private LocalDateTime shareTime;

    // 分享平台（如：微信、微博、QQ）
    private String sharePlatform;
}

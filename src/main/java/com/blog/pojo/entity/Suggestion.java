package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 建议表实体类
 *
 * @Author worlt
 * @Date 2026/1/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String title;

    private String content;

    // 状态 0:未实现/待处理 1:已采纳 2:已实现 3:已拒绝
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

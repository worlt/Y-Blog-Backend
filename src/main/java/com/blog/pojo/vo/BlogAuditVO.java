package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAuditVO {

    private Long id;

    private String title;

    private String content;

    private String cover;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long userId;

    private String username;

    private Long categoryId;

    private String category;

    private Integer status;

    private Long reviewerId;

    private List<String> tagList;

    private List<String> customTagList;

}

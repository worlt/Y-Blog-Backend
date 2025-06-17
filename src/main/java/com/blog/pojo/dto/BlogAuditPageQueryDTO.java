package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/31 下午4:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAuditPageQueryDTO {

    private String title;

    private String username;

    private Integer status;

    private int page;

    private int pageSize;
}

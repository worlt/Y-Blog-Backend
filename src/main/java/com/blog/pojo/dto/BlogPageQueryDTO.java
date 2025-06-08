package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogPageQueryDTO {

    private String title;

    private Integer page;

    private Integer pageSize;

    private Long userId;

    private Integer status;

}

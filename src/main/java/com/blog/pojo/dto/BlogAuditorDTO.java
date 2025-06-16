package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/6/16 下午5:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAuditorDTO {

    private Long blogId;

    private String blogTitle;

    private Long reviewerId;

    private Integer status;

    private String message;

}

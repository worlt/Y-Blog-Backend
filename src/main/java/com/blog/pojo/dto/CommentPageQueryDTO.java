package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/30 上午9:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentPageQueryDTO {

    public String content;

    public Long userId;

    public Long blogId;

    public int page;

    public int pageSize;

}

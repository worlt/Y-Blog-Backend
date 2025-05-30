package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/30 上午9:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentAddDTO {

    public String content;

    public Long userId;

    public Long blogId;

    public Long parentId;

}

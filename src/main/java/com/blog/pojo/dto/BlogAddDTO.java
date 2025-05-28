package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAddDTO implements Serializable {

    private String title;

    private String content;

    private Long userId;

    private Long categoryId;

    private List<Long> tagIdList;

    private List<String> customTagList;

}

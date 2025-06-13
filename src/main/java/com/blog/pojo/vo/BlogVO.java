package com.blog.pojo.vo;

import com.blog.pojo.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午9:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogVO {

    private Long id;

    private String title;

    private String content;

    // 创建时间
    private LocalDateTime createTime;

    // 发布时间
    private LocalDateTime publishTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 浏览量
    private Long views;

    // 点赞量
    private Long likes;

    // 点踩量
    private Long dislikes;

    // 收藏量
    private Long collections;

    // 分享量
    private Long shares;

    // 评论量
    private Long comments;

    private Long userId;

    private String username;

    private String avatar;

    private Long categoryId;

    private String category;

//    private List<Long> tagIdList;

    private List<String> tagList;

//    private List<Long> customTagIdList;

    private List<String> customTagList;

    private Integer status;
}

package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 建议投票表实体类
 *
 * @Author worlt
 * @Date 2026/1/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionVote implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long suggestionId;

    private Long userId;

    // 投票类型 0:不想要 1:想要
    private Integer type;

    private LocalDateTime createTime;
}

package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审核记录表：记录审核员对文章或用户的操作
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 审核员id
    private Long reviewerId;

    private Long blogId;

    private Long userId;

    // 审核时间
    private LocalDateTime reviewTime;

    // 审核结果
    private Integer reviewResult;

    private String reviewReason;

    // 审核员发消息给作者（整改时使用）
    private String messageToAuthor;

}

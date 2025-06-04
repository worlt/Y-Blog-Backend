package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客举报实体
 * @Author worlt
 * @Date 2025/6/3 下午6:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 举报唯一标识符
     */
    private Long id;
    
    /**
     * 举报用户ID
     */
    private Long userId;
    
    /**
     * 被举报博客ID
     */
    private Long blogId;
    
    /**
     * 举报时间
     */
    private LocalDateTime reportTime;
    
    /**
     * 举报原因
     */
    private String reportReason;
    
    /**
     * 举报状态（0-待审核、1-处理中、2-已处理）
     */
    private Integer status;
    
    /**
     * 审核员ID
     */
    private Long reviewerId;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 审核结果（0-正常、1-整改、2-永久封禁）
     */
    private Integer reviewResult;

}

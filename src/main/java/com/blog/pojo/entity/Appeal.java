package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户账号申诉表
 *
 * @Author worlt
 * @Date 2025/6/4 下午9:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appeal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申诉唯一标识符
     */
    private Long id;
    
    /**
     * 申诉用户ID
     */
    private Long userId;
    
    /**
     * 申诉时间
     */
    private LocalDateTime appealTime;
    
    /**
     * 申诉原因
     */
    private String appealReason;
    
    /**
     * 申诉状态（0-待审核、1-处理中、2-已处理）
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
     * 审核结果（0-接受、1-拒绝）
     */
    private Integer reviewResult;
    
    /**
     * 审核原因
     */
    private String reviewReason;
    
    /**
     * 申诉邮箱
     */
    private String email;
}

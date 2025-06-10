package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话实体类
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private Long id;
    
    /**
     * 用户1ID（较小的用户ID）
     */
    private Long user1Id;
    
    /**
     * 用户2ID（较大的用户ID）
     */
    private Long user2Id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 最后一条消息预览
     */
    private String lastMessage;
    
    /**
     * 用户1未读消息数
     */
    private Integer unreadCount1;
    
    /**
     * 用户2未读消息数
     */
    private Integer unreadCount2;
    
    /**
     * 会话类型：0-系统通知，1-用户私聊
     */
    private Integer type;
}

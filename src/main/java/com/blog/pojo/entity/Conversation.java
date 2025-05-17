package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话表：存储用户之间的会话
 *
 * @Author worlt
 * @Date 2025/5/16 下午9:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 对话用户A的ID
    private Long user1Id;

    // 对话用户B的ID
    private Long user2Id;

    // 会话创建时间
    private LocalDateTime createTime;

    // 会话更新时间
    private LocalDateTime updateTime;

}

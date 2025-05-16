package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    private Long id;

    private String name;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

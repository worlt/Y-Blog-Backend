package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 标签分页查询
 *
 * @Author worlt
 * @Date 2025/6/16 下午2:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagPageQueryDTO implements Serializable {

    // 标签名称
    private String name;

    // 页码
    private int page;

    // 每页记录数
    private int pageSize;
} 
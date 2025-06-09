package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分类DTO
 *
 * @Author worlt
 * @Date 2025/6/16 上午10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // ID，新增时不需要，更新时必须
    private Long id;
    
    // 分类名称
    private String name;
    
    // 分类描述
    private String description;
} 
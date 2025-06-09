package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 标签DTO
 *
 * @Author worlt
 * @Date 2025/6/16 上午10:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // ID，新增时不需要，更新时必须
    private Long id;
    
    // 标签名称
    private String name;
} 
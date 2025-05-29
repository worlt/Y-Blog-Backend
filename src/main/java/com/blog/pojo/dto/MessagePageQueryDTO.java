package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/29 下午2:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessagePageQueryDTO {

    public String content;

    public int page;

    public int pageSize;
}

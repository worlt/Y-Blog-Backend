package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/6/2 下午1:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditorPageQueryDTO {

    private String name;

    private int page;

    private int pageSize;
}

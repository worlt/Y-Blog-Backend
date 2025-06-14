package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/20 下午10:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPageQueryDTO {

    private String username;

    private int page;

    private Integer status;

    private int pageSize;

}

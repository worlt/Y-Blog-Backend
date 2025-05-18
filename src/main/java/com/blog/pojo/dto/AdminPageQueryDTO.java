package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员查询
 *
 * @Author worlt
 * @Date 2025/5/18 下午10:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminPageQueryDTO implements Serializable {

    // 姓名
    private String name;

    // 页码
    private int page;

    // 每页记录数
    private int pageSize;
}

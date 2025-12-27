package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogPageQueryDTO {

    private String title;

    private Integer page;

    private Integer pageSize;

    private Long userId;

    private String username;

    private Integer categoryId;

    private Integer status;

    // 排序字段
    private String sortField;

    // 排序方式（asc/desc）
    private String sortOrder;

    // 是否忽略置顶排序（true时按正常排序逻辑，不优先显示置顶）
    private Boolean ignoreTop;

    // 置顶状态筛选（1-置顶，0-非置顶，null-不筛选）
    private Integer isTop;

}

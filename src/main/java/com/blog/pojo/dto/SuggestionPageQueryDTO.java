package com.blog.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SuggestionPageQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    // 状态 0:未实现 1:已实现 null:全部
    private Integer status;
    // 排序 1:最新 2:最热
    private Integer sort;
}

package com.blog.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 *
 * @Author worlt
 * @Date 2025/5/18 下午10:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    // 总记录数
    private Long total;

    // 当前页数的集合
    private List records;

}

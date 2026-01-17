package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 网站访问统计实体类
 * 
 * @Author worlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteVisit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 访问IP
     */
    private String ip;

    /**
     * 访问日期
     */
    private LocalDate visitDate;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastVisitTime;

    /**
     * 当日访问次数
     */
    private Integer count;
}

package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 分享统计VO
 *
 * @Author worlt
 * @Date 2025/6/15 下午4:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // 博客ID
    private Long blogId;
    
    // 总分享次数
    private Integer totalShares;
    
    // 各平台分享统计（key为平台名称，value为分享次数）
    private Map<String, Integer> platformShares;
} 
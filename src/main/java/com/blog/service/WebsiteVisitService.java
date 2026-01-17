package com.blog.service;

import java.util.Map;

public interface WebsiteVisitService {

    /**
     * 记录访问
     */
    void recordVisit(String ip, Long userId);

    /**
     * 获取最近 days 天的统计数据
     */
    Map<String, Object> getStats(int days);

    /**
     * 获取最近的访问记录列表
     */
    java.util.List<com.blog.pojo.entity.WebsiteVisit> getRecentVisits(int limit);

    /**
     * 获取今日用户访问分布统计
     */
    Map<String, Object> getTodayUserStats();
}

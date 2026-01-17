package com.blog.service.impl;

import com.blog.mapper.WebsiteVisitMapper;
import com.blog.pojo.entity.WebsiteVisit;
import com.blog.service.WebsiteVisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WebsiteVisitServiceImpl implements WebsiteVisitService {

    @Autowired
    private WebsiteVisitMapper websiteVisitMapper;

    @Override
    public void recordVisit(String ip, Long userId) {
        LocalDate today = LocalDate.now();
        // 根据IP和日期查找记录（当天同IP视为同一人）
        List<WebsiteVisit> visits = websiteVisitMapper.selectByIpAndDate(ip, today);

        if (visits != null && !visits.isEmpty()) {
            // 已存在记录，取第一条（理论上由于逻辑控制，每天每个IP应该只有一条，除非历史数据脏）
            WebsiteVisit visit = visits.get(0);

            // 逻辑1：如果当前请求有UserId，而数据库记录没有，则关联用户（视为登录）
            // 如果数据库已有UserId，即使当前请求没传UserId（退出后访问），也不置空，保持原记录
            if (userId != null && visit.getUserId() == null) {
                websiteVisitMapper.updateUserId(visit.getId(), userId);
            }

            // 逻辑2：检查距离上次访问是否超过30分钟
            LocalDateTime lastTime = visit.getLastVisitTime();
            LocalDateTime now = LocalDateTime.now();

            // 只有当距离上次访问超过30分钟时，才增加计数并更有时间
            if (lastTime.plusMinutes(30).isBefore(now)) {
                websiteVisitMapper.updateCountAndTime(visit.getId(), now);
            }
        } else {
            // 不存在，插入新记录
            WebsiteVisit newVisit = WebsiteVisit.builder()
                    .ip(ip)
                    .userId(userId)
                    .visitDate(today)
                    .lastVisitTime(LocalDateTime.now())
                    .count(1)
                    .build();
            websiteVisitMapper.insert(newVisit);
        }
    }

    @Override
    public Map<String, Object> getStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查数据库
        List<Map<String, Object>> statsList = websiteVisitMapper.selectStats(startDate, endDate);

        // 补全日期（如果某天没有数据，也需要返回0）
        List<String> dateList = new ArrayList<>();
        List<Long> pvList = new ArrayList<>();
        List<Long> uvList = new ArrayList<>();

        Map<String, Map<String, Object>> statsMap = new HashMap<>();
        for (Map<String, Object> item : statsList) {
            // MySQL 返回的 date 可能是 java.sql.Date
            String dateStr = item.get("date").toString();
            statsMap.put(dateStr, item);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.format(fmt);
            dateList.add(dateStr);

            if (statsMap.containsKey(dateStr)) {
                Map<String, Object> item = statsMap.get(dateStr);
                // Number 类型转换安全处理
                pvList.add(Long.parseLong(item.get("pv").toString()));
                uvList.add(Long.parseLong(item.get("uv").toString()));
            } else {
                pvList.add(0L);
                uvList.add(0L);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dateList", dateList);
        result.put("pvList", pvList);
        result.put("uvList", uvList);
        return result;
    }

    @Override
    public List<WebsiteVisit> getRecentVisits(int limit) {
        return websiteVisitMapper.selectRecent(limit);
    }

    @Override
    public Map<String, Object> getTodayUserStats() {
        return websiteVisitMapper.selectUserTypeStats(LocalDate.now());
    }
}

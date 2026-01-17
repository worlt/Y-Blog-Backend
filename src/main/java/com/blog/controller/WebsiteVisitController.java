package com.blog.controller;

import com.blog.result.Result;
import com.blog.service.WebsiteVisitService;
import com.blog.context.BaseContext;
import com.blog.utils.IpUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class WebsiteVisitController {

    @Autowired
    private WebsiteVisitService websiteVisitService;

    /**
     * 记录访问 (供User端调用, 无需权限)
     */
    @PostMapping("/visit")
    public Result<?> recordVisit(HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        // 尝试获取当前登录用户ID，如果没有则为null
        // BaseContext 在拦截器中设置，如果没有通过拦截器可能为空，或者是异常
        // 我们假设拦截器会对放行的接口也尝试解析token但不强制拦截
        // 或者简单点，让前端传过来？不，前端传不可信。
        // 如果是拦截器放行的接口，BaseContext 可能没有值。
        // 这里需要确认 TokenInterceptor 的逻辑。
        // 简单处理：如果 BaseContext 有值则取，否则 null。
        Long userId = null;
        try {
            userId = BaseContext.getCurrentId();
        } catch (Exception e) {
            // ignore
        }

        websiteVisitService.recordVisit(ip, userId);
        return Result.success();
    }

    /**
     * 获取统计数据 (供Admin端调用)
     * 默认近 7 天
     */
    @GetMapping("/admin/visit/stats")
    public Result<Map<String, Object>> getStats(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> stats = websiteVisitService.getStats(days);
        return Result.success(stats);
    }

    /**
     * 获取最近访问列表 (供Admin端调用)
     */
    @GetMapping("/admin/visit/list")
    public Result<java.util.List<com.blog.pojo.entity.WebsiteVisit>> getVisitList(
            @RequestParam(defaultValue = "20") int limit) {
        return Result.success(websiteVisitService.getRecentVisits(limit));
    }

    /**
     * 获取今日用户分布统计
     */
    @GetMapping("/admin/visit/today")
    public Result<Map<String, Object>> getTodayStats() {
        return Result.success(websiteVisitService.getTodayUserStats());
    }
}

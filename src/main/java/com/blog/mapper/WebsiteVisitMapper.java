package com.blog.mapper;

import com.blog.pojo.entity.WebsiteVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebsiteVisitMapper {

    /**
     * 根据唯一键查询记录
     */
    WebsiteVisit selectByUniqueKey(@Param("ip") String ip, @Param("userId") Long userId,
            @Param("visitDate") LocalDate visitDate);

    /**
     * 根据IP和日期查询记录（列表，防重复）
     */
    List<WebsiteVisit> selectByIpAndDate(@Param("ip") String ip, @Param("visitDate") LocalDate visitDate);

    /**
     * 查询最近的访问记录
     */
    List<WebsiteVisit> selectRecent(@Param("limit") int limit);

    /**
     * 统计指定日期的用户和访客数量
     * return map: {userCount: 10, guestCount: 5}
     */
    Map<String, Object> selectUserTypeStats(@Param("date") LocalDate date);

    /**
     * 插入新记录
     */
    void insert(WebsiteVisit visit);

    /**
     * 更新访问次数和时间
     */
    void updateCountAndTime(@Param("id") Long id, @Param("time") java.time.LocalDateTime time);

    /**
     * 更新用户ID
     */
    void updateUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 统计指定日期范围内的 PV (PageView) 和 UV (Unique Visitor)
     * 返回列表，每天一条记录
     * Map keys: "date", "pv", "uv"
     */
    List<Map<String, Object>> selectStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

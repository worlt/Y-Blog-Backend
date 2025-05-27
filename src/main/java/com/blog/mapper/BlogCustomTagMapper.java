package com.blog.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/5/26 下午9:57
 */
@Mapper
public interface BlogCustomTagMapper {

    @MapKey("id")
    Map<Long, List<String>> getCustomTagsByBlogIds(List<Long> blogIds);
}

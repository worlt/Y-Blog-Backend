package com.blog.mapper;

import com.blog.pojo.entity.BlogCustomTag;
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

    /**
     * 批量插入自定义标签
     * @param blogCustomTags
     */
    void insertBatch(List<BlogCustomTag> blogCustomTags);
}

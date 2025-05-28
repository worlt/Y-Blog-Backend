package com.blog.mapper;

import com.blog.pojo.entity.BlogTag;
import com.blog.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/5/26 下午9:45
 */
@Mapper
public interface BlogTagMapper {

    @MapKey("id")
    Map<Long, List<String>> getTagsByBlogIds(List<Long> blogIds);

    /**
     * 批量出啊如标签
     * @param blogTags
     */
    void insertBatch(List<BlogTag> blogTags);

    /**
     * 根据博客id删除标签
     * @param id
     */
    @Delete("delete from blog_tag where blog_id = #{id}")
    void deleteByBlogId(Long id);
}

package com.blog.mapper;

import com.blog.pojo.entity.BlogTag;
import com.blog.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    
    /**
     * 根据标签id删除博客标签关联
     * @param tagId 标签ID
     */
    @Delete("delete from blog_tag where tag_id = #{tagId}")
    void deleteByTagId(Long tagId);
    
    /**
     * 统计使用指定标签的博客数量
     * @param tagId 标签ID
     * @return 博客数量
     */
    @Select("select count(*) from blog_tag where tag_id = #{tagId}")
    Integer countByTagId(Long tagId);

    /**
     * 根据博客id查询标签
     * @param id
     * @return
     */
    @Select("select name from tag " +
            "join blog_tag bt on tag.id = bt.tag_id " +
            "where bt.blog_id = #{id}")
    List<String> getByBlogId(Long id);
}

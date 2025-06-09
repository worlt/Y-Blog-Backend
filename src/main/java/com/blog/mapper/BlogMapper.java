package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.BlogVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:11
 */
@Mapper
public interface BlogMapper {

    /**
     * 博客文章分页查询
     * @param blogPageQueryDTO
     * @return
     */
    public Page<BlogVO> page(BlogPageQueryDTO blogPageQueryDTO);

    /**
     * 添加博客
     * @param blog
     */
    void add(Blog blog);

    /**
     * 根据id查询博客
     * @param id
     * @return
     */
    @Select("select * from blog where id = #{id}")
    Blog getById(Long id);

    /**
     * 根据id删除博客
     * @param id
     */
    @Delete("delete from blog where id = #{id}")
    void deleteById(Long id);

    /**
     * 更新博客
     * @param blog
     */
    void update(Blog blog);

    /**
     * 更新博客状态
     * @param id
     * @param status
     */
    @Update("update blog set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 获取作者id
     * @param id
     * @return
     */
    @Select("select user_id from blog where id = #{id}")
    Long getUserId(Long id);
    
    /**
     * 统计指定分类下的博客数量
     * @param categoryId 分类ID
     * @return 博客数量
     */
    @Select("select count(*) from blog where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 增加博客浏览量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET views = views + 1, update_time = NOW() WHERE id = #{blogId}")
    void incrementViews(@Param("blogId") Long blogId);
    
    /**
     * 增加博客点赞量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET likes = likes + 1, update_time = NOW() WHERE id = #{blogId}")
    void incrementLikes(@Param("blogId") Long blogId);
    
    /**
     * 减少博客点赞量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET likes = CASE WHEN likes > 0 THEN likes - 1 ELSE 0 END, update_time = NOW() WHERE id = #{blogId}")
    void decrementLikes(@Param("blogId") Long blogId);
    
    /**
     * 增加博客点踩量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET dislikes = dislikes + 1, update_time = NOW() WHERE id = #{blogId}")
    void incrementDislikes(@Param("blogId") Long blogId);
    
    /**
     * 减少博客点踩量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET dislikes = CASE WHEN dislikes > 0 THEN dislikes - 1 ELSE 0 END, update_time = NOW() WHERE id = #{blogId}")
    void decrementDislikes(@Param("blogId") Long blogId);
    
    /**
     * 增加博客收藏量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET collections = collections + 1, update_time = NOW() WHERE id = #{blogId}")
    void incrementCollections(@Param("blogId") Long blogId);
    
    /**
     * 减少博客收藏量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET collections = CASE WHEN collections > 0 THEN collections - 1 ELSE 0 END, update_time = NOW() WHERE id = #{blogId}")
    void decrementCollections(@Param("blogId") Long blogId);
    
    /**
     * 增加博客分享量
     * @param blogId 博客ID
     */
    @Update("UPDATE blog SET shares = shares + 1, update_time = NOW() WHERE id = #{blogId}")
    void incrementShares(@Param("blogId") Long blogId);
    
    /**
     * 获取所有博客ID
     * @return 博客ID列表
     */
    @Select("SELECT id FROM blog")
    List<Long> getAllBlogIds();
}

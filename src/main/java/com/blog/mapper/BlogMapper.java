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
     * @return 影响的行数
     */
    @Update("UPDATE blog SET views = views + 1 WHERE id = #{blogId}")
    int incrementViews(@Param("blogId") Long blogId);

    /**
     * 增加博客点赞数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET likes = likes + 1 WHERE id = #{blogId}")
    int incrementLikes(@Param("blogId") Long blogId);

    /**
     * 减少博客点赞数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET likes = likes - 1 WHERE id = #{blogId} AND likes > 0")
    int decrementLikes(@Param("blogId") Long blogId);

    /**
     * 增加博客收藏数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET collections = collections + 1 WHERE id = #{blogId}")
    int incrementCollections(@Param("blogId") Long blogId);

    /**
     * 减少博客收藏数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET collections = collections - 1 WHERE id = #{blogId} AND collections > 0")
    int decrementCollections(@Param("blogId") Long blogId);

    /**
     * 增加博客评论数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET comments = comments + 1 WHERE id = #{blogId}")
    int incrementComments(@Param("blogId") Long blogId);

    /**
     * 减少博客评论数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET comments = comments - 1 WHERE id = #{blogId} AND comments > 0")
    int decrementComments(@Param("blogId") Long blogId);

    /**
     * 获取所有博客ID
     * @return 博客ID列表
     */
    @Select("SELECT id FROM blog")
    List<Long> getAllBlogIds();

    /**
     * 校正博客点赞数
     * @return 影响的行数
     */
    @Update("UPDATE blog b SET likes = (SELECT COUNT(*) FROM `like` l WHERE l.blog_id = b.id)")
    int correctLikesCount();

    /**
     * 校正博客收藏数
     * @return 影响的行数
     */
    @Update("UPDATE blog b SET collections = (SELECT COUNT(*) FROM collection c WHERE c.blog_id = b.id)")
    int correctCollectionsCount();

    /**
     * 校正博客评论数
     * @return 影响的行数
     */
    @Update("UPDATE blog b SET comments = (SELECT COUNT(*) FROM comment c WHERE c.blog_id = b.id AND c.status = 0)")
    int correctCommentsCount();

    /**
     * 增加博客点踩数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET dislikes = dislikes + 1 WHERE id = #{blogId}")
    int incrementDislikes(@Param("blogId") Long blogId);

    /**
     * 减少博客点踩数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET dislikes = dislikes - 1 WHERE id = #{blogId} AND dislikes > 0")
    int decrementDislikes(@Param("blogId") Long blogId);

    /**
     * 增加博客分享数
     * @param blogId 博客ID
     * @return 影响的行数
     */
    @Update("UPDATE blog SET shares = shares + 1 WHERE id = #{blogId}")
    int incrementShares(@Param("blogId") Long blogId);
}

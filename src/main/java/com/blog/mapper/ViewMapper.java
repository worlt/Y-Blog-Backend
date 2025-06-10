package com.blog.mapper;

import com.blog.pojo.entity.View;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 浏览记录Mapper接口
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Mapper
public interface ViewMapper {
    
    /**
     * 添加浏览记录
     * @param view 浏览记录对象
     * @return 影响的行数
     */
    int insert(View view);
    
    /**
     * 查询用户是否浏览过指定博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return 浏览记录对象
     */
    @Select("select * from view where user_id = #{userId} and blog_id = #{blogId} limit 1")
    View findByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 查询博客的浏览记录
     * @param blogId 博客id
     * @return 浏览记录列表
     */
    @Select("select * from view where blog_id = #{blogId}")
    List<View> findByBlogId(Long blogId);
    
    /**
     * 查询用户的浏览记录
     * @param userId 用户id
     * @return 浏览记录列表
     */
    @Select("select * from view where user_id = #{userId} order by view_time desc")
    List<View> findByUserId(Long userId);
    
    /**
     * 统计博客被浏览的次数
     * @param blogId 博客id
     * @return 浏览次数
     */
    @Select("select count(*) from view where blog_id = #{blogId}")
    int countByBlogId(Long blogId);

    /**
     * 更新浏览记录
     * @param view
     */
    @Update("update view set user_id = #{userId}, blog_id = #{blogId}, view_time = #{viewTime} where id = #{id}")
    void update(View view);
}
package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.View;
import com.blog.pojo.vo.BlogVO;
import com.github.pagehelper.Page;
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
     * 
     * @param view 浏览记录对象
     * @return 影响的行数
     */
    int insert(View view);

    /**
     * 查询用户是否浏览过指定博客
     * 
     * @param userId 用户id
     * @param blogId 博客id
     * @return 浏览记录对象
     */
    @Select("select * from view where user_id = #{userId} and blog_id = #{blogId} limit 1")
    View findByUserIdAndBlogId(Long userId, Long blogId);

    /**
     * 根据IP和博客ID查询浏览记录
     * 
     * @param ip     IP地址
     * @param blogId 博客ID
     * @return 浏览记录
     */
    View findByIpAndBlogId(@Param("ip") String ip, @Param("blogId") Long blogId);

    /**
     * 查询博客的浏览记录
     * 
     * @param blogId 博客id
     * @return 浏览记录列表
     */
    @Select("select * from view where blog_id = #{blogId}")
    List<View> findByBlogId(Long blogId);

    /**
     * 查询用户的浏览记录
     * 
     * @param userId 用户id
     * @return 浏览记录列表
     */
    @Select("select * from view where user_id = #{userId} order by view_time desc")
    List<View> findByUserId(Long userId);

    /**
     * 统计博客被浏览的次数
     * 
     * @param blogId 博客id
     * @return 浏览次数
     */
    @Select("select count(*) from view where blog_id = #{blogId}")
    int countByBlogId(Long blogId);

    /**
     * 更新浏览记录
     * 
     * @param view
     */
    @Update("update view set user_id = #{userId}, blog_id = #{blogId}, view_time = #{viewTime} where id = #{id}")
    void update(View view);

    /**
     * 分页查询用户的浏览记录
     * 
     * @param blogPageQueryDTO 分页查询参数
     * @return 博客VO列表（分页）
     */
    Page<BlogVO> getUserViewHistoryPage(BlogPageQueryDTO blogPageQueryDTO);

    /**
     * 统计用户博客的总浏览量
     * 
     * @param userId 用户ID
     * @return 用户博客的总浏览量
     */
    @Select("SELECT COALESCE(SUM(b.views), 0) FROM blog b WHERE b.user_id = #{userId}")
    Integer countUserBlogViews(Long userId);
}
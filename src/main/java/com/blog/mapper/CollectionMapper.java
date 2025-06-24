package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Collection;
import com.blog.pojo.vo.CollectionVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Mapper
public interface CollectionMapper {
    
    /**
     * 添加收藏记录
     * @param collection 收藏对象
     * @return 影响的行数
     */
    int insert(Collection collection);
    
    /**
     * 查询用户是否收藏了指定博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return 收藏对象
     */
    @Select("select * from collection where user_id = #{userId} and blog_id = #{blogId}")
    Collection findByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 删除收藏记录
     * @param userId 用户id
     * @param blogId 博客id
     * @return 影响的行数
     */
    @Delete("delete from collection where user_id = #{userId} and blog_id = #{blogId}")
    int deleteByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 查询用户的所有收藏记录
     * @param userId 用户id
     * @return 收藏列表
     */
    @Select("select * from collection where user_id = #{userId} order by collect_time desc")
    List<Collection> findByUserId(Long userId);
    
    /**
     * 分页查询用户收藏的博客
     * @param blogPageQueryDTO 分页查询参数
     * @return 收藏列表（分页）
     */
    Page<CollectionVO> pageByUserId(BlogPageQueryDTO blogPageQueryDTO);
    
    /**
     * 统计博客被收藏的次数
     * @param blogId 博客id
     * @return 收藏次数
     */
    @Select("select count(*) from collection where blog_id = #{blogId}")
    int countByBlogId(Long blogId);
} 
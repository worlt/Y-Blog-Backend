package com.blog.mapper;

import com.blog.pojo.entity.Collection;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CollectionMapper {
    
    /**
     * 添加收藏记录
     * @param collection 收藏对象
     * @return 影响的行数
     */
    @Insert("insert into collection(user_id, blog_id, collect_time) values(#{userId}, #{blogId}, #{collectTime})")
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
     * 统计博客被收藏的次数
     * @param blogId 博客id
     * @return 收藏次数
     */
    @Select("select count(*) from collection where blog_id = #{blogId}")
    int countByBlogId(Long blogId);
} 
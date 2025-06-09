package com.blog.mapper;

import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.vo.CommentVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/29 下午10:27
 */
@Mapper
public interface CommentMapper {

    /**
     * 分页查询评论
     * @param commentPageQueryDTO
     * @return
     */
    Page<CommentVO> page(CommentPageQueryDTO commentPageQueryDTO);

    /**
     * 新增评论
     * @param comment
     */
    void add(Comment comment);

    /**
     * 根据id删除评论
     * @param id
     */
    @Delete("delete from comment where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询评论详情
     *
     * @param id
     * @return
     */
    CommentVO getVOById(Long id);

    /**
     * 根据博客id删除该博客所有的评论
     * @param id
     */
    @Delete("delete from comment where blog_id = #{id}")
    void deleteByBlogId(Long id);

    /**
     * 根据父评论id删除子评论
     * @param id
     */
    @Delete("delete from comment where parent_id = #{id}")
    void deleteChildCommentByParentId(Long id);

    /**
     * 递归删除评论以及其子评论
     * @param id
     */
    void deleteCommentWithChildren(Long id);

    /**
     * 根据id查询评论
     * @param id
     * @return
     */
    @Select("select * from comment where id = #{id}")
    Comment getById(Long id);

    /**
     * 获取当前博客所有评论
     * @param id
     */
    @Select("select * from comment where blog_id = #{id}")
    List<Comment> getByBlogId(Long id);

    /**
     * 获取评论用户的id
     * @param id
     * @return
     */
    @Select("select user_id from comment where id = #{id}")
    Long getUserId(Long id);

    /**
     * 获取用户的所有评论
     * @param userId
     * @return
     */
    @Select("select * from comment where user_id = #{userId}")
    List<Comment> getByUserId(Long userId);
}

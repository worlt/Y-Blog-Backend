package com.blog.mapper;

import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.vo.CommentVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

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
     * @param id
     * @return
     */
    CommentVO getById(Long id);
}

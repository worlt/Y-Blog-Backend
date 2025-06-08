package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.BlogVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

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
    void updateStatus(Long id, Integer status);

    /**
     * 获取作者id
     * @param id
     * @return
     */
    @Select("select user_id from blog where id = #{id}")
    Long getUserId(Long id);
}

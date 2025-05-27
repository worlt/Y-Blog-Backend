package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.BlogVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

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

}

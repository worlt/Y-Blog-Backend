package com.blog.mapper;

import com.blog.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author worlt
 * @Date 2025/5/21 下午9:24
 */
@Mapper
public interface CategoryMapper {

    /**
     * 根据id查询分类
     * @param categoryId
     */
    @Select("select * from category where id = #{id}")
    Category getById(Long categoryId);
}

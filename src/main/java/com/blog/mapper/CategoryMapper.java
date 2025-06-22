package com.blog.mapper;

import com.blog.pojo.dto.CategoryPageQueryDTO;
import com.blog.pojo.entity.Category;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午9:24
 */
@Mapper
public interface CategoryMapper {

    /**
     * 根据id查询分类
     * @param id 分类ID
     * @return 分类对象
     */
    @Select("select * from category where id = #{id}")
    Category getById(Long id);
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    @Select("select * from category order by id asc")
    List<Category> listAll();
    
    /**
     * 新增分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int insert(Category category);
    
    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int update(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    @Delete("delete from category where id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类对象
     */
    @Select("select * from category where name = #{name} limit 1")
    Category getByName(String name);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);
    
    /**
     * 获取热门分类
     * 通过与博客表关联查询使用频率最高的分类
     * @param limit 限制数量
     * @return 热门分类列表
     */
    @Select("SELECT c.* FROM category c " +
            "LEFT JOIN blog b ON c.id = b.category_id " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(b.category_id) DESC " +
            "LIMIT #{limit}")
    List<Category> getHotCategories(Integer limit);
}

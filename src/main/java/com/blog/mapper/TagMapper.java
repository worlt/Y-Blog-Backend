package com.blog.mapper;

import com.blog.pojo.dto.TagPageQueryDTO;
import com.blog.pojo.entity.Tag;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午9:23
 */
@Mapper
public interface TagMapper {

    /**
     * 根据id查询标签
     * @param id 标签ID
     * @return 标签对象
     */
    @Select("select * from tag where id = #{id}")
    Tag getById(Long id);
    
    /**
     * 查询所有标签
     * @return 标签列表
     */
    @Select("select * from tag order by id asc")
    List<Tag> listAll();
    
    /**
     * 新增标签
     * @param tag 标签对象
     * @return 影响的行数
     */
    int insert(Tag tag);
    
    /**
     * 更新标签
     * @param tag 标签对象
     * @return 影响的行数
     */
    int update(Tag tag);
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 影响的行数
     */
    @Delete("delete from tag where id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据名称查询标签
     * @param name 标签名称
     * @return 标签对象
     */
    @Select("select * from tag where name = #{name} limit 1")
    Tag getByName(String name);

    /**
     * 标签分页查询
     *
     * @param tagPageQueryDTO
     * @return
     */
    Page<Tag> page(TagPageQueryDTO tagPageQueryDTO);
}

package com.blog.mapper;

import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.vo.BlogAuditVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:23
 */
@Mapper
public interface BlogAuditMapper {

    /**
     * 分页查询博客审核
     * @param blogAuditPageQueryDTO
     * @return
     */
    Page<BlogAuditVO> pageQuery(BlogAuditPageQueryDTO blogAuditPageQueryDTO);

    /**
     * 根据id查询审核博客的详情
     * @param id
     * @return
     */
    @Select("select b.id, b.title, b.content, b.create_time, b.update_time, b.user_id, b.category_id, b.status, u.username " +
            "from blog b left join user u on b.user_id = u.id " +
            "where b.id = #{id}")
    BlogAuditVO getById(Long id);

}

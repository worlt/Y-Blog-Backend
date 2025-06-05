package com.blog.mapper;

import com.blog.pojo.dto.AuditorPageQueryDTO;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:23
 */
@Mapper
public interface AuditorMapper {

    /**
     * 分页查询审核员
     * @param auditorPageQueryDTO
     * @return
     */
    Page<AuditorVO> page(AuditorPageQueryDTO auditorPageQueryDTO);

    /**
     * 添加
     * @param auditor
     */
    @Insert("insert into auditor(name, username, password, email, avatar, create_time, update_time, status, role, gender) " +
            "values (#{name}, #{username}, #{password}, #{email}, #{avatar}, #{createTime}, #{updateTime}, #{status}, #{role}, #{gender})")
    void add(Auditor auditor);

    /**
     * 根据id删除审核员
     * @param id
     */
    @Delete("delete from auditor where id = #{id}")
    void deleteById(Long id);

    /**
     * 编辑审核员
     * @param auditor
     */
    void update(Auditor auditor);

    /**
     * 根据id查询审核员
     *
     * @param id
     * @return
     */
    @Select("select * from auditor where id = #{id}")
    Auditor getById(Long id);

    /**
     * 根据用户名查询审核员
     * @param username
     * @return
     */
    @Select("select * from auditor where username = #{username}")
    Auditor getByUsername(String username);
}

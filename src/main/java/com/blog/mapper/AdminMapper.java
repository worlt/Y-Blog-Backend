package com.blog.mapper;

import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.entity.Admin;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author worlt
 * @Date 2025/5/18 下午9:22
 */
@Mapper
public interface AdminMapper {

    /**
     * 根据用户名查询管理员
     *
     * @param username
     * @return
     */
    @Select("select * from admin where username = #{username}")
    Admin getUsername(String username);

    /**
     * 新增管理员
     * @param admin
     */
    @Insert("insert into admin (name, username, password, email, gender, avatar, create_time, update_time, status, role)" +
            "values " +
            "(#{name}, #{username}, #{password}, #{email}, #{gender}, #{avatar}, #{createTime}, #{updateTime}, #{status}, #{role})")
    void insert(Admin admin);

    /**
     * 更新管理员信息
     *
     * @param admin
     */
    void update(Admin admin);

    /**
     * 分页查询
     *
     * @param adminPageQueryDTO
     * @return
     */
    Page<Admin> pageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     * 根据id查询管理员
     *
     * @param id
     * @return
     */
    @Select("select * from admin where id = #{id}")
    Admin getById(Long id);

    /**
     * 根据id删除管理员
     * @param id
     */
    @Delete("delete from admin where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据邮箱查询管理员
     * @param email
     * @return
     */
    @Select("select * from admin where email = #{email}")
    Admin getByEmail(String email);
}

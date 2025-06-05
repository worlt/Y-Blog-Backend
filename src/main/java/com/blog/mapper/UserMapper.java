package com.blog.mapper;

import com.blog.pojo.dto.UserPageQueryDTO;
import com.blog.pojo.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author worlt
 * @Date 2025/5/20 下午10:23
 */
@Mapper
public interface UserMapper {

    /**
     * 用户分页查询
     * @param userPAgeQueryDTO
     * @return
     */
    public Page<User> pageQuery(UserPageQueryDTO userPAgeQueryDTO);

    /**
     * 新增用户
     * @param user
     */
    @Insert("insert into user(username, password, phone, email, openid, unionid, gender, avatar," +
            " create_time, update_time, status, role, fans)" +
            "values " +
            "(#{username}, #{password}, #{phone}, #{email}, #{openid}, #{unionid}, #{gender}, #{avatar}," +
            " #{createTime}, #{updateTime}, #{status}, #{role}, #{fans})")
    void add(User user);

    /**
     * 编辑用户信息
     * @param user
     */
    void update(User user);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    @Select("select * from user where email = #{email}")
    User getByEmail(String email);

    /**
     * 根据id删除用户
     * @param id
     */
    @Delete("delete from user where id = #{id}")
    void deleteById(Long id);
    
    /**
     * 更新用户状态
     * @param id
     * @param status
     */
    @Update("update user set status = #{status} where id = #{id}")
    void updateStatus(Long id, Integer status);
}

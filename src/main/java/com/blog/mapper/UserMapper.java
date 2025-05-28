package com.blog.mapper;

import com.blog.pojo.dto.UserPageQueryDTO;
import com.blog.pojo.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Insert("insert into user(username, password, phone, openid, unionid, gender, avatar," +
            " create_time, update_time, status, role, fans)" +
            "value " +
            "(#{username}, #{password}, #{phone}, #{openid}, #{unionid}, #{gender}, #{avatar}," +
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
     * 根据id删除用户
     * @param id
     */
    @Delete("delete from user where id = #{id}")
    void deleteById(Long id);
}

package com.blog.mapper;

import com.blog.pojo.dto.UserPAgeQueryDTO;
import com.blog.pojo.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
    public Page<User> pageQuery(UserPAgeQueryDTO userPAgeQueryDTO);

    /**
     * 新增用户
     * @param user
     */
    @Insert("insert into user(username, password, phone, openid, unionid, gender, avatar," +
            " create_time, update_time, status, role, fans)" +
            "values " +
            "(#{username}, #{password}, #{phone}, #{openid}, #{unionid}, #{gender}, #{avatar}," +
            " #{createTime}, #{updateTime}, #{status}, #{role}, #{fans})")
    void add(User user);
}

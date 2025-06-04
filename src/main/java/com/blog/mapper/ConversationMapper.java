package com.blog.mapper;

import com.blog.pojo.entity.Conversation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * @Author worlt
 * @Date 2025/5/21 下午9:23
 */
@Mapper
public interface ConversationMapper {

    /**
     * 新增会话
     *
     * @param conversation
     * @return
     */
    @Insert("insert into conversation(create_time, update_time) values (#{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long add(Conversation conversation);
}

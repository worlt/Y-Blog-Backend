package com.blog.mapper;

import com.blog.pojo.entity.Conversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 会话Mapper接口
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Mapper
public interface ConversationMapper {
    
    /**
     * 创建会话
     * @param conversation 会话对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO conversation(user1_id, user2_id, create_time, update_time, last_message, unread_count_1, unread_count_2, type) " +
            "VALUES(#{user1Id}, #{user2Id}, NOW(), NOW(), #{lastMessage}, #{unreadCount1}, #{unreadCount2}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Conversation conversation);
    
    /**
     * 获取用户参与的所有会话
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM conversation WHERE user1_id = #{userId} OR user2_id = #{userId} ORDER BY update_time DESC")
    List<Conversation> findByUserId(Long userId);
    
    /**
     * 根据ID获取会话
     * @param id 会话ID
     * @return 会话对象
     */
    @Select("SELECT * FROM conversation WHERE id = #{id}")
    Conversation getById(Long id);
    
    /**
     * 根据两个用户ID获取会话
     * @param smallerId 较小的用户ID
     * @param largerId 较大的用户ID
     * @return 会话对象
     */
    @Select("SELECT * FROM conversation WHERE user1_id = #{smallerId} AND user2_id = #{largerId} LIMIT 1")
    Conversation findByUserPair(@Param("smallerId") Long smallerId, @Param("largerId") Long largerId);
    
    /**
     * 更新会话最后一条消息
     * @param conversation 会话对象
     * @return 影响的行数
     */
    @Update("UPDATE conversation SET last_message = #{lastMessage}, update_time = NOW() WHERE id = #{id}")
    int updateLastMessage(Conversation conversation);
    
    /**
     * 递增用户1的未读消息数
     * @param conversationId 会话ID
     * @return 影响的行数
     */
    @Update("UPDATE conversation SET unread_count_1 = unread_count_1 + 1 WHERE id = #{conversationId}")
    int incrementUnreadCount1(@Param("conversationId") Long conversationId);
    
    /**
     * 递增用户2的未读消息数
     * @param conversationId 会话ID
     * @return 影响的行数
     */
    @Update("UPDATE conversation SET unread_count_2 = unread_count_2 + 1 WHERE id = #{conversationId}")
    int incrementUnreadCount2(@Param("conversationId") Long conversationId);
    
    /**
     * 重置用户1的未读消息数
     * @param conversationId 会话ID
     * @return 影响的行数
     */
    @Update("UPDATE conversation SET unread_count_1 = 0 WHERE id = #{conversationId}")
    int resetUnreadCount1(@Param("conversationId") Long conversationId);
    
    /**
     * 重置用户2的未读消息数
     * @param conversationId 会话ID
     * @return 影响的行数
     */
    @Update("UPDATE conversation SET unread_count_2 = 0 WHERE id = #{conversationId}")
    int resetUnreadCount2(@Param("conversationId") Long conversationId);
    
    /**
     * 统计用户未读消息总数
     * @param userId 用户ID
     * @return 未读消息总数
     */
    @Select("SELECT SUM(CASE WHEN user1_id = #{userId} THEN unread_count_1 ELSE unread_count_2 END) " +
            "FROM conversation WHERE user1_id = #{userId} OR user2_id = #{userId}")
    int countUserUnread(@Param("userId") Long userId);
    
    /**
     * 获取用户所有有未读消息的会话
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM conversation WHERE (user1_id = #{userId} AND unread_count_1 > 0) OR (user2_id = #{userId} AND unread_count_2 > 0)")
    List<Conversation> findUnreadByUserId(@Param("userId") Long userId);
}

package com.blog.mapper;

import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.entity.Message;
import com.blog.pojo.vo.MessageVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/29 下午1:48
 */
@Mapper
public interface MessageMapper {

    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    Page<MessageVO> page(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 新增消息
     * @param message
     */
    void add(Message message);

    /**
     * 根据id查询消息详情
     * @param id
     * @return
     */
    @Select("select * from message where id = #{id}")
    MessageVO getVOById(Long id);

    /**
     * 根据id删除消息
     * @param id
     */
    @Delete("delete from message where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据接收者
     * @param receiverId
     * @param messageType
     * @return
     */
    Long getConversationByReceiverIdAndMessageType(Long receiverId, Integer messageType);

    /**
     * 插入消息
     * @param message 消息对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO message(conversation_id, sender_id, receiver_id, content, send_time, is_read, message_type) " +
            "VALUES(#{conversationId}, #{senderId}, #{receiverId}, #{content}, NOW(), #{isRead}, #{messageType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);
    
    /**
     * 获取会话中的消息
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM message WHERE conversation_id = #{conversationId} ORDER BY send_time ASC")
    List<Message> findByConversationId(Long conversationId);
    
    /**
     * 将收到的消息标记为已读
     * @param conversationId 会话ID
     * @param receiverId 接收者ID
     * @return 成功更新的消息数量
     */
    @Update("UPDATE message SET is_read = 1 WHERE conversation_id = #{conversationId} AND receiver_id = #{receiverId} AND is_read = 0")
    int markAsRead(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);
    
    /**
     * 统计用户在某个会话中的未读消息数
     * @param conversationId 会话ID
     * @param receiverId 接收者ID
     * @return 未读消息数
     */
    @Select("SELECT COUNT(*) FROM message WHERE conversation_id = #{conversationId} AND receiver_id = #{receiverId} AND is_read = 0")
    int countUnreadInConversation(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);

    /**
     * 获取消息
     * @param id
     * @return
     */
    @Select("select * from message where id = #{id}")
    Message getById(Long id);

    /**
     * 更新消息
     * @param message
     */
    void update(Message message);
}

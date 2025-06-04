package com.blog.mapper;

import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.entity.Message;
import com.blog.pojo.vo.MessageVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    MessageVO getById(Long id);

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
    Long getConversationByReceiverIdAndMessageType(Long receiverId, String messageType);
}

package com.blog.mapper;

import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.vo.MessageVO;
import com.github.pagehelper.Page;
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

}

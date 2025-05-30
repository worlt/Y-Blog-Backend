package com.blog.service.impl;

import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.mapper.MessageMapper;
import com.blog.pojo.dto.MessageAddDTO;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.entity.Message;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.PageResult;
import com.blog.service.MessageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/29 下午1:49
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    @Override
    public PageResult page(MessagePageQueryDTO messagePageQueryDTO) {
        PageHelper.startPage(messagePageQueryDTO.getPage(), messagePageQueryDTO.getPageSize());
        Page<MessageVO> page = messageMapper.page(messagePageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增消息
     * @param messageAddDTO
     */
    @Override
    public void add(MessageAddDTO messageAddDTO) {
        Message message = new Message();
        BeanUtils.copyProperties(messageAddDTO, message);

        message.setSendTime(LocalDateTime.now());
        message.setIsRead(MessageStatusConstant.MESSAGE_UNREAD);

        messageMapper.add(message);
    }

    /**
     * 根据id查询消息详情
     * @param id
     * @return
     */
    @Override
    public MessageVO getById(Long id) {
        return messageMapper.getById(id);
    }
}

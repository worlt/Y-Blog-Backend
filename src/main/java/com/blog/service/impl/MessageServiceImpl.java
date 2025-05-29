package com.blog.service.impl;

import com.blog.mapper.MessageMapper;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.PageResult;
import com.blog.service.MessageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

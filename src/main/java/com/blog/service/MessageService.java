package com.blog.service;

import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.result.PageResult;

/**
 * @Author worlt
 * @Date 2025/5/29 下午1:49
 */
public interface MessageService {

    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    PageResult page(MessagePageQueryDTO messagePageQueryDTO);
}

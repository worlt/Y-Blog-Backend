package com.blog.service;

import com.blog.pojo.dto.MessageAddDTO;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.vo.MessageVO;
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

    /**
     * 新增消息
     * @param messageAddDTO
     */
    void add(MessageAddDTO messageAddDTO);

    /**
     * 根据id查询消息详情
     * @param id
     * @return
     */
    MessageVO getById(Long id);

    /**
     * 根据id删除消息
     * @param id
     */
    void deleteById(Long id);
}

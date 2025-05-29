package com.blog.controller.admin;

import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信息管理
 *
 * @Author worlt
 * @Date 2025/5/29 下午1:46
 */
@RestController
@RequestMapping("/admin/message")
@Api(tags = "消息相关接口")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;


    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询消息")
    public Result<PageResult> page(MessagePageQueryDTO messagePageQueryDTO) {
        log.info("分页查询消息：{}", messagePageQueryDTO);
        PageResult pageResult = messageService.page(messagePageQueryDTO);
        return Result.success(pageResult);
    }



}

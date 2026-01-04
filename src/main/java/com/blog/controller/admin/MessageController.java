package com.blog.controller.admin;

import com.blog.pojo.dto.MessageAddDTO;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 信息管理
 *
 * @Author worlt
 * @Date 2025/5/29 下午1:46
 */
@RestController("adminMessageController")
@RequestMapping("/admin/message")
@Api(tags = "消息相关接口")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页查询消息
     * 
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

    /**
     * 新增消息
     * 
     * @param messageAddDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增消息")
    public Result add(@RequestBody MessageAddDTO messageAddDTO) {
        log.info("新增消息：{}", messageAddDTO);
        messageService.add(messageAddDTO);
        return Result.success();
    }

    /**
     * 根据消息id查询消息详情
     * 
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询消息详情")
    public Result<MessageVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询消息详情：{}", id);
        MessageVO messageVO = messageService.getVOById(id);
        return Result.success(messageVO);
    }

    /**
     * 根据id删除消息
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除消息")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除消息：{}", id);
        messageService.deleteById(id);
        return Result.success();
    }

}

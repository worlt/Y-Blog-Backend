package com.blog.controller.user;

import com.blog.constant.statusConstant.AppealStatusConstant;
import com.blog.pojo.entity.Appeal;
import com.blog.pojo.vo.AppealVO;
import com.blog.result.Result;
import com.blog.service.AppealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户申诉控制器
 * 
 * @Author worlt
 * @Date 2025/6/4 下午9:30
 */
@RestController
@RequestMapping("/user/appeal")
@Slf4j
@Api(tags = "用户申诉相关接口")
public class UserAppealController {
    
    @Autowired
    private AppealService appealService;
    
    /**
     * 提交申诉
     * @param appeal
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("提交申诉")
    public Result<String> submit(@RequestBody Appeal appeal) {
        log.info("提交申诉：{}", appeal);
        // 调用服务提交申诉
        appealService.submit(appeal);
        
        return Result.success("申诉提交成功，请等待审核");
    }
    
    /**
     * 查询申诉状态
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询申诉状态")
    public Result<AppealVO> getById(@PathVariable Long id) {
        log.info("查询申诉状态：{}", id);
        AppealVO appealVO = appealService.getById(id);
        return Result.success(appealVO);
    }
} 
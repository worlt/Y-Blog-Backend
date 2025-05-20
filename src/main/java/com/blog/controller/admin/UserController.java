package com.blog.controller.admin;

import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserPAgeQueryDTO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 *
 * @Author worlt
 * @Date 2025/5/20 下午10:18
 */
@RestController
@RequestMapping("admin/user")
@Slf4j
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户分页查询
     * @param userPAgeQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("用户分页查询")
    public Result<PageResult> page(UserPAgeQueryDTO userPAgeQueryDTO) {
        log.info("用户分页查询：{}", userPAgeQueryDTO);
        PageResult pageResult = userService.pageQuery(userPAgeQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增用户")
    public Result add(@RequestBody UserDTO userDTO) {
        log.info("新增用户：{}", userDTO);
        userService.add(userDTO);
        return Result.success();
    }

}

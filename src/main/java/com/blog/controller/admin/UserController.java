package com.blog.controller.admin;

import com.blog.pojo.dto.UserDTO;
import com.blog.pojo.dto.UserPageQueryDTO;
import com.blog.pojo.entity.User;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理
 *
 * @Author worlt
 * @Date 2025/5/20 下午10:18
 */
@RestController("adminUserController")
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
    public Result<PageResult> page(UserPageQueryDTO userPAgeQueryDTO) {
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

    /**
     * 编辑用户信息
     * @param userDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑用户信息")
    public Result update(@RequestBody UserDTO userDTO) {
        log.info("编辑用户信息：{}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }

    /**
     * 启用禁用用户
     * @param status
     * @param map
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("启用禁用用户")
    public Result setStatus(@PathVariable("status") Integer status, @RequestBody Map<String, Long> map) {
        Long id = map.get("id");
        log.info("启用禁用用户：id：{}，status：{}", id, status);
        userService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除用户")
    public Result deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询用户")
    public Result<User> getById(@PathVariable("id") Long id) {
        log.info("根据id查询用户：{}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }

}

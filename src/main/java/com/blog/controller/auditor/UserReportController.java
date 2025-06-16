package com.blog.controller.auditor;

import com.blog.pojo.dto.UserReportDTO;
import com.blog.pojo.dto.UserReportPageQueryDTO;
import com.blog.pojo.vo.UserReportVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.UserReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/6/4 下午1:03
 */
@RestController("auditorUserController")
@RequestMapping("/auditor/userReport")
@Slf4j
@Api(tags = "用户举报相关接口")
public class UserReportController {

    @Autowired
    private UserReportService userReportService;

    /**
     * 分页查询用户举报列表
     * @param userReportPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("用户举报分页查询")
    public Result<PageResult> page(UserReportPageQueryDTO userReportPageQueryDTO) {
        log.info("用户举报分页查询：{}", userReportPageQueryDTO);
        PageResult pageResult = userReportService.pageQuery(userReportPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询用户举报详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询用户举报详情")
    public Result<UserReportVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询用户举报详情：{}", id);
        UserReportVO userReportVO = userReportService.getById(id);
        return Result.success(userReportVO);
    }

    /**
     * 处理用户举报
     * @param userReportDTO
     * @return
     */
    @PutMapping("/handle")
    @ApiOperation("处理用户举报")
    public Result handle(@RequestBody UserReportDTO userReportDTO) {
        log.info("处理用户举报：{}", userReportDTO);
        userReportService.handle(userReportDTO);
        return Result.success();
    }

    /**
     * 更新用户举报状态
     * @param map
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("更新用户举报状态")
    public Result setStatus(@RequestBody Map<String, Long> map, @PathVariable("status") Integer status) {
        Long id = map.get("id");
        log.info("更新用户举报状态：id：{}，status：{}", id, status);
        userReportService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 根据id删除用户举报
     * @param id
     * @return
     */
//    @DeleteMapping("/deleteById/{id}")
//    @ApiOperation("根据id删除用户举报")
//    public Result deleteById(@PathVariable("id") Long id) {
//        log.info("根据id删除用户举报：{}", id);
//        userReportService.deleteById(id);
//        return Result.success();
//    }

    /**
     * 批量删除用户举报
     * @param ids
     * @return
     */
//    @DeleteMapping("/deleteByIds")
//    @ApiOperation("批量删除用户举报")
//    public Result deleteByIds(@RequestParam("ids") Long[] ids) {
//        log.info("批量删除用户举报：{}", (Object) ids);
//        userReportService.deleteByIds(ids);
//        return Result.success();
//    }
}

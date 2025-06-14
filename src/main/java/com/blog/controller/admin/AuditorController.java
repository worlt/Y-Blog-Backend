package com.blog.controller.admin;

import com.blog.pojo.dto.AuditorDTO;
import com.blog.pojo.dto.AuditorPageQueryDTO;
import com.blog.pojo.vo.AdminVO;
import com.blog.pojo.vo.AuditorVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.AuditorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author worlt
 * @Date 2025/6/2 下午12:56
 */
@RestController("adminAuditorController")
@RequestMapping("/admin/auditor")
@Slf4j
@Api(tags = "审核员相关接口")
public class AuditorController {

    @Autowired
    private AuditorService auditorService;

    /**
     * 分页查询审核员
     * @param auditorPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询审核员")
    public Result<PageResult> page(AuditorPageQueryDTO auditorPageQueryDTO) {
        log.info("分页查询审核员：{}", auditorPageQueryDTO);
        PageResult pageResult = auditorService.page(auditorPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 添加审核员
     * @param auditorDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加审核员")
    public Result add(AuditorDTO auditorDTO) {
        log.info("添加审核员：{}", auditorDTO);
        auditorService.add(auditorDTO);
        return Result.success();
    }

    /**
     * 根据id删除审核员
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除审核员")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除审核员：{}", id);
        auditorService.deleteById(id);
        return Result.success();
    }

    /**
     * 编辑审核员
     * @param auditorDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑审核员")
    public Result update(@RequestBody AuditorDTO auditorDTO) {
        log.info("编辑审核员，{}", auditorDTO);
        auditorService.update(auditorDTO);
        return Result.success();
    }

    /**
     * 根据id查询审核员
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询审核员")
    public Result<AuditorVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询审核员：{}", id);
        AuditorVO adminVO = auditorService.getById(id);
        return Result.success(adminVO);
    }

    /**
     * 设置审核员状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("设置审核员状态")
    public Result setStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status) {
        log.info("设置审核员状态：id：{}，status：{}", id, status);
        auditorService.setStatus(id, status);
        return Result.success();
    }

}

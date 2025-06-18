package com.blog.controller.auditor;

import com.blog.constant.statusConstant.AppealStatusConstant;
import com.blog.pojo.dto.AppealDTO;
import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.vo.AppealVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.AppealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author worlt
 * @Date 2025/6/4 下午9:08
 */
@RestController
@RequestMapping("/auditor/appeal")
@Slf4j
@Api(tags = "申诉相关接口")
public class AppealController {
    
    @Autowired
    private AppealService appealService;

    /**
     * 分页查询
     * @param appealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询申诉")
    public Result<PageResult> page(AppealPageQueryDTO appealPageQueryDTO) {
        log.info("分页查询申诉：{}", appealPageQueryDTO);
        PageResult pageResult = appealService.pageQuery(appealPageQueryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 根据id查询申诉详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询申诉详情")
    public Result<AppealVO> getById(@PathVariable Long id) {
        log.info("根据id查询申诉详情：{}", id);
        AppealVO appealVO = appealService.getById(id);
        return Result.success(appealVO);
    }
    
    /**
     * 处理申诉
     * @param appealDTO
     * @return
     */
    @PutMapping("/handle")
    @ApiOperation("处理申诉")
    public Result handle(@RequestBody AppealDTO appealDTO) {
        log.info("处理申诉：{}", appealDTO);
        appealService.handle(appealDTO);
        return Result.success();
    }
    
    /**
     * 设置申诉为处理中
     * @param id
     * @return
     */
    @PutMapping("/inProcess/{id}")
    @ApiOperation("设置申诉为处理中")
    public Result inProcess(@PathVariable Long id) {
        log.info("设置申诉为处理中：{}", id);
        appealService.setStatus(id, AppealStatusConstant.IN_PROCESS);
        return Result.success();
    }
    
    /**
     * 根据id删除申诉
     * @param id
     * @return
     */
//    @DeleteMapping("/{id}")
//    @ApiOperation("根据id删除申诉")
//    public Result deleteById(@PathVariable Long id) {
//        log.info("根据id删除申诉：{}", id);
//        appealService.deleteById(id);
//        return Result.success();
//    }
    
    /**
     * 批量删除申诉
     * @param ids
     * @return
     */
//    @DeleteMapping("/batch")
//    @ApiOperation("批量删除申诉")
//    public Result deleteByIds(@RequestParam Long[] ids) {
//        log.info("批量删除申诉：{}", ids);
//        appealService.deleteByIds(ids);
//        return Result.success();
//    }
}

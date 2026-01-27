package com.blog.controller.admin;

import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.SuggestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminSuggestionController")
@RequestMapping("/admin/suggestion")
@Slf4j
@Api(tags = "管理端建议接口")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @GetMapping("/list")
    @ApiOperation("建议列表")
    public Result<PageResult> list(SuggestionPageQueryDTO queryDTO) {
        PageResult result = suggestionService.list(queryDTO, true);
        return Result.success(result);
    }

    @PutMapping("/status/{id}/{status}")
    @ApiOperation("修改建议状态")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        suggestionService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除建议")
    public Result delete(@PathVariable Long id) {
        log.info("删除建议：{}", id);
        suggestionService.delete(id);
        return Result.success();
    }
}

package com.blog.controller.user;

import com.blog.pojo.dto.SuggestionAddDTO;
import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.pojo.dto.SuggestionVoteDTO;
import com.blog.pojo.vo.SuggestionVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.SuggestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userSuggestionController")
@RequestMapping("/user/suggestion")
@Slf4j
@Api(tags = "用户建议接口")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping
    @ApiOperation("新增建议")
    public Result add(@RequestBody SuggestionAddDTO addDTO) {
        suggestionService.addSuggestion(addDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("建议列表")
    public Result<PageResult> list(SuggestionPageQueryDTO queryDTO) {
        PageResult result = suggestionService.list(queryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("建议详情")
    public Result<SuggestionVO> getById(@PathVariable Long id) {
        SuggestionVO suggestion = suggestionService.getById(id);
        return Result.success(suggestion);
    }

    @PostMapping("/vote")
    @ApiOperation("建议投票")
    public Result vote(@RequestBody SuggestionVoteDTO voteDTO) {
        suggestionService.vote(voteDTO);
        return Result.success();
    }
}

package com.blog.controller;

import com.blog.pojo.dto.CaptchaVerifyDTO;
import com.blog.pojo.vo.CaptchaPuzzleVO;
import com.blog.pojo.vo.CaptchaVerifyVO;
import com.blog.result.Result;
import com.blog.service.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 滑动验证码控制器
 *
 * @Author cascade
 * @Date 2025/12/10
 */
@RestController
@RequestMapping("/captcha")
@Api(tags = "滑动验证码接口")
@Slf4j
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取拼图
     *
     * @param difficulty 难度等级: easy/normal/hard
     * @return 拼图数据
     */
    @GetMapping("/puzzle")
    @ApiOperation("获取滑动验证码拼图")
    public Result<CaptchaPuzzleVO> getPuzzle(
            @ApiParam(value = "难度等级", defaultValue = "normal")
            @RequestParam(value = "difficulty", defaultValue = "normal") String difficulty) {
        log.info("获取拼图: difficulty={}", difficulty);
        
        CaptchaPuzzleVO puzzle = captchaService.generatePuzzle(difficulty);
        return Result.success(puzzle);
    }

    /**
     * 验证拼图
     *
     * @param captchaVerifyDTO 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    @ApiOperation("验证滑动验证码")
    public Result<CaptchaVerifyVO> verifyPuzzle(@RequestBody CaptchaVerifyDTO captchaVerifyDTO) {
        log.info("验证拼图: puzzleId={}, offsetX={}, offsetY={}, difficulty={}", 
                captchaVerifyDTO.getPuzzleId(),
                captchaVerifyDTO.getOffsetX(),
                captchaVerifyDTO.getOffsetY(),
                captchaVerifyDTO.getDifficulty());

        CaptchaVerifyVO result = captchaService.verifyPuzzle(
                captchaVerifyDTO.getPuzzleId(),
                captchaVerifyDTO.getOffsetX(),
                captchaVerifyDTO.getOffsetY(),
                captchaVerifyDTO.getDifficulty());

        if (result.getCaptchaToken() != null) {
            return Result.success(result);
        } else {
            return Result.error("拼图验证失败，请重试");
        }
    }

}

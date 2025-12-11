package com.blog.service;

import com.blog.pojo.vo.CaptchaPuzzleVO;
import com.blog.pojo.vo.CaptchaVerifyVO;

/**
 * 滑动验证码服务接口
 *
 * @Author cascade
 * @Date 2025/12/10
 */
public interface CaptchaService {

    /**
     * 生成拼图
     *
     * @param difficulty 难度等级: easy/normal/hard
     * @return 拼图数据
     */
    CaptchaPuzzleVO generatePuzzle(String difficulty);

    /**
     * 验证拼图
     *
     * @param puzzleId 拼图ID
     * @param offsetX 用户拖动的X偏移量
     * @param offsetY 用户拖动的Y偏移量
     * @param difficulty 难度等级
     * @return 验证结果
     */
    CaptchaVerifyVO verifyPuzzle(String puzzleId, Integer offsetX, Integer offsetY, String difficulty);

    /**
     * 验证captchaToken是否有效
     *
     * @param captchaToken 验证码token
     * @return 是否有效
     */
    boolean verifyCaptchaToken(String captchaToken);

    /**
     * 检查Redis是否可用
     *
     * @return 是否可用
     */
    boolean isRedisAvailable();

}

package com.blog.service.impl;

import com.blog.pojo.vo.CaptchaPuzzleVO;
import com.blog.pojo.vo.CaptchaVerifyVO;
import com.blog.service.CaptchaService;
import com.blog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 滑动验证码服务实现类
 *
 * @Author cascade
 * @Date 2025/12/10
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    // Redis键前缀
    private static final String PUZZLE_KEY_PREFIX = "captcha:puzzle:";
    private static final String TOKEN_KEY_PREFIX = "captcha:token:";

    // 拼图过期时间（分钟）
    private static final int PUZZLE_EXPIRE_MINUTES = 5;

    // 验证token过期时间（分钟）
    private static final int TOKEN_EXPIRE_MINUTES = 10;

    // 容差范围（像素）
    private static final Map<String, Integer> TOLERANCE_MAP = new HashMap<>();

    static {
        TOLERANCE_MAP.put("easy", 10);
        TOLERANCE_MAP.put("normal", 5);
        TOLERANCE_MAP.put("hard", 2);
    }

    // 背景图尺寸
    private static final int BG_WIDTH = 300;
    private static final int BG_HEIGHT = 150;

    // 拼图块尺寸
    private static final int PUZZLE_WIDTH = 50;
    private static final int PUZZLE_HEIGHT = 50;

    /**
     * 生成拼图
     *
     * @param difficulty 难度等级: easy/normal/hard
     * @return 拼图数据
     */
    @Override
    public CaptchaPuzzleVO generatePuzzle(String difficulty) {
        // 检查Redis是否可用
        if (!isRedisAvailable()) {
            log.info("Redis不可用，降级到前端验证模式");
            return generateFrontendModePuzzle(difficulty);
        }

        try {
            // 生成拼图ID
            String puzzleId = UUID.randomUUID().toString();

            // 生成随机背景图（渐变色）
            String backgroundImage = generateRandomGradient();

            // 生成随机拼图块位置
            int offsetX = (int) (Math.random() * (BG_WIDTH - PUZZLE_WIDTH));
            int offsetY = (int) (Math.random() * (BG_HEIGHT - PUZZLE_HEIGHT));

            // 拼图块颜色
            String puzzleImage = "rgba(255, 255, 255, 0.3)";

            // 存储拼图数据到Redis
            Map<String, Object> puzzleData = new HashMap<>();
            puzzleData.put("offsetX", offsetX);
            puzzleData.put("offsetY", offsetY);
            puzzleData.put("difficulty", difficulty);
            puzzleData.put("createdAt", System.currentTimeMillis());

            String key = PUZZLE_KEY_PREFIX + puzzleId;
            redisTemplate.opsForValue().set(key, puzzleData, PUZZLE_EXPIRE_MINUTES, TimeUnit.MINUTES);

            log.info("生成拼图: puzzleId={}, offsetX={}, offsetY={}, difficulty={}", 
                    puzzleId, offsetX, offsetY, difficulty);

            return CaptchaPuzzleVO.builder()
                    .puzzleId(puzzleId)
                    .backgroundImage(backgroundImage)
                    .puzzleImage(puzzleImage)
                    .offsetX(offsetX)
                    .offsetY(offsetY)
                    .difficulty(difficulty)
                    .mode("backend")
                    .build();

        } catch (Exception e) {
            log.error("生成拼图失败，降级到前端验证模式", e);
            return generateFrontendModePuzzle(difficulty);
        }
    }

    /**
     * 验证拼图
     *
     * @param puzzleId 拼图ID
     * @param offsetX 用户拖动的X偏移量
     * @param offsetY 用户拖动的Y偏移量
     * @param difficulty 难度等级
     * @return 验证结果
     */
    @Override
    public CaptchaVerifyVO verifyPuzzle(String puzzleId, Integer offsetX, Integer offsetY, String difficulty) {
        // 检查Redis是否可用
        if (!isRedisAvailable()) {
            log.info("Redis不可用，降级到前端验证模式");
            return generateFrontendModeToken();
        }

        try {
            // 从Redis获取拼图数据
            String key = PUZZLE_KEY_PREFIX + puzzleId;
            Map<String, Object> puzzleData = (Map<String, Object>) redisTemplate.opsForValue().get(key);

            if (puzzleData == null) {
                log.warn("拼图数据不存在或已过期: puzzleId={}", puzzleId);
                return CaptchaVerifyVO.builder()
                        .captchaToken(null)
                        .mode("backend")
                        .build();
            }

            // 获取正确的偏移量
            Integer correctOffsetX = (Integer) puzzleData.get("offsetX");
            Integer correctOffsetY = (Integer) puzzleData.get("offsetY");
            String storedDifficulty = (String) puzzleData.get("difficulty");

            // 获取容差范围
            Integer tolerance = TOLERANCE_MAP.getOrDefault(difficulty, 5);

            // 验证偏移量
            int diffX = Math.abs(offsetX - correctOffsetX);
            int diffY = Math.abs(offsetY - correctOffsetY);

            if (diffX <= tolerance && diffY <= tolerance) {
                // 验证成功，生成验证token
                String captchaToken = UUID.randomUUID().toString();

                // 存储验证token到Redis
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("puzzleId", puzzleId);
                tokenData.put("verified", true);
                tokenData.put("createdAt", System.currentTimeMillis());

                String tokenKey = TOKEN_KEY_PREFIX + captchaToken;
                redisTemplate.opsForValue().set(tokenKey, tokenData, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);

                // 删除拼图数据
                redisTemplate.delete(key);

                log.info("拼图验证成功: puzzleId={}, offsetX={}, offsetY={}, diffX={}, diffY={}, tolerance={}", 
                        puzzleId, offsetX, offsetY, diffX, diffY, tolerance);

                return CaptchaVerifyVO.builder()
                        .captchaToken(captchaToken)
                        .mode("backend")
                        .build();
            } else {
                log.warn("拼图验证失败: puzzleId={}, offsetX={}, offsetY={}, diffX={}, diffY={}, tolerance={}", 
                        puzzleId, offsetX, offsetY, diffX, diffY, tolerance);
                return CaptchaVerifyVO.builder()
                        .captchaToken(null)
                        .mode("backend")
                        .build();
            }

        } catch (Exception e) {
            log.error("验证拼图失败，降级到前端验证模式", e);
            return generateFrontendModeToken();
        }
    }

    /**
     * 验证captchaToken是否有效
     *
     * @param captchaToken 验证码token
     * @return 是否有效
     */
    @Override
    public boolean verifyCaptchaToken(String captchaToken) {
        // 检查Redis是否可用
        if (!isRedisAvailable()) {
            log.info("Redis不可用，跳过captchaToken验证");
            return true;
        }

        try {
            if (captchaToken == null || captchaToken.isEmpty()) {
                return false;
            }

            String key = TOKEN_KEY_PREFIX + captchaToken;
            Map<String, Object> tokenData = (Map<String, Object>) redisTemplate.opsForValue().get(key);

            if (tokenData == null) {
                log.warn("验证token不存在或已过期: captchaToken={}", captchaToken);
                return false;
            }

            // 删除token（一次性使用）
            redisTemplate.delete(key);

            log.info("验证token有效: captchaToken={}", captchaToken);
            return true;

        } catch (Exception e) {
            log.error("验证captchaToken失败", e);
            return false;
        }
    }

    /**
     * 检查Redis是否可用
     *
     * @return 是否可用
     */
    @Override
    public boolean isRedisAvailable() {
        try {
            // 尝试执行一个简单的Redis操作
            redisTemplate.opsForValue().set("captcha:health:check", "ok", 1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.warn("Redis不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成前端模式的拼图（降级）
     *
     * @param difficulty 难度等级
     * @return 拼图数据
     */
    private CaptchaPuzzleVO generateFrontendModePuzzle(String difficulty) {
        String backgroundImage = generateRandomGradient();
        String puzzleImage = "rgba(255, 255, 255, 0.3)";

        return CaptchaPuzzleVO.builder()
                .puzzleId(null)
                .backgroundImage(backgroundImage)
                .puzzleImage(puzzleImage)
                .difficulty(difficulty)
                .mode("frontend")
                .build();
    }

    /**
     * 生成前端模式的验证token（降级）
     *
     * @return 验证结果
     */
    private CaptchaVerifyVO generateFrontendModeToken() {
        String captchaToken = UUID.randomUUID().toString();
        return CaptchaVerifyVO.builder()
                .captchaToken(captchaToken)
                .mode("frontend")
                .build();
    }

    /**
     * 生成随机渐变色背景
     *
     * @return 渐变色CSS
     */
    private String generateRandomGradient() {
        String[] gradients = {
                "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
                "linear-gradient(135deg, #fa709a 0%, #fee140 100%)"
        };
        return gradients[(int) (Math.random() * gradients.length)];
    }

}

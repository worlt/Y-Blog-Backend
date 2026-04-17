package com.blog.utils;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 敏感词过滤工具类
 * 基于 houbb/sensitive-word
 * 
 * @author worlt
 */
@Slf4j
public class SensitiveUtils {

    /**
     * 替换文本中的敏感词，默认替换为 *
     * 
     * @param text 待处理文本
     * @return 脱敏后的文本
     */
    public static String replace(String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }

        // 检查是否包含敏感词
        if (SensitiveWordHelper.contains(text)) {
            String result = SensitiveWordHelper.replace(text);
            log.info("检测到并脱敏了敏感词内容");
            return result;
        }

        return text;
    }

    /**
     * 检查是否包含敏感词
     * 
     * @param text 待处理文本
     * @return 是否包含
     */
    public static boolean contains(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        return SensitiveWordHelper.contains(text);
    }
}

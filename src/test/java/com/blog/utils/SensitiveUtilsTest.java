package com.blog.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SensitiveUtilsTest {

    @Test
    public void testReplace() {
        // 默认敏感词库通常包含一些常见词汇，这里测试一些通用的
        String text = "这是一个包含不当内容的测试字符串";
        // 注意：具体的敏感词取决于 houbb/sensitive-word 的默认库。
        // 如果默认库没包含，这个测试可能通过但也可能说明没起作用。
        // 我们测试一个通常会被认为是敏感的词汇（假设库里有）
        String sensitiveText = "色情网站是一个不良网站";
        String result = SensitiveUtils.replace(sensitiveText);
        
        System.out.println("Original: " + sensitiveText);
        System.out.println("Result: " + result);
        
        assertNotEquals(sensitiveText, result);
        assertTrue(result.contains("*"));
    }
}

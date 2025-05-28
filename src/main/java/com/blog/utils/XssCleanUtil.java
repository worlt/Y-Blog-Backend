package com.blog.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import lombok.extern.slf4j.Slf4j;

/**
 * XSS攻击防护工具类
 * 用于清理可能包含恶意脚本的HTML内容
 */
@Slf4j
public class XssCleanUtil {

    /**
     * 清理可能的XSS攻击，仅允许基本的格式化标签
     * @param html 需要清理的HTML内容
     * @return 清理后的安全HTML
     */
    public static String clean(String html) {
        if (html == null) {
            return null;
        }
        String cleaned = Jsoup.clean(html, Safelist.basic());
        logPotentialXss(html, cleaned, "basic");
        return cleaned;
    }

    /**
     * 清理可能的XSS攻击，允许更多富文本编辑器常用标签
     * 适用于博客内容等需要丰富格式的场景
     * @param html 需要清理的HTML内容
     * @return 清理后的安全HTML
     */
    public static String cleanRichText(String html) {
        if (html == null) {
            return null;
        }
        
        // 基于relaxed构建自定义安全列表
        Safelist safelist = Safelist.relaxed()
                // 允许常见的样式属性
                .addAttributes(":all", "style", "class")
                // 允许图片的data-src属性(常见于懒加载)
                .addAttributes("img", "data-src")
                // 允许代码块标签
                .addTags("code", "pre");
        
        String cleaned = Jsoup.clean(html, safelist);
        logPotentialXss(html, cleaned, "richtext");
        return cleaned;
    }

    /**
     * 完全清理所有HTML标签，只保留文本
     * 适用于标题、摘要等不需要HTML格式的场景
     * @param html 包含HTML的内容
     * @return 纯文本内容
     */
    public static String cleanAllTags(String html) {
        if (html == null) {
            return null;
        }
        String cleaned = Jsoup.clean(html, Safelist.none());
        logPotentialXss(html, cleaned, "alltags");
        return cleaned;
    }
    
    /**
     * 转义HTML特殊字符，防止XSS攻击
     * 适用于不需要HTML格式的用户输入，如评论、用户名等
     * @param text 需要转义的文本
     * @return 转义后的安全文本
     */
    public static String escape(String text) {
        if (text == null) {
            return null;
        }
        String escaped = text.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;");
        
        if (!escaped.equals(text)) {
            log.warn("潜在的XSS注入尝试被转义: 原文本包含特殊字符");
        }
        return escaped;
    }
    
    /**
     * 记录潜在的XSS攻击尝试
     * @param original 原始输入
     * @param cleaned 清理后的输入
     * @param cleanType 清理类型
     */
    private static void logPotentialXss(String original, String cleaned, String cleanType) {
        if (!original.equals(cleaned)) {
            // 检查是否有常见的XSS攻击特征
            boolean containsScriptTag = original.toLowerCase().contains("<script");
            boolean containsEventHandler = original.toLowerCase().matches(".*\\s+on\\w+\\s*=.*");
            boolean containsJavaScriptUrl = original.toLowerCase().matches(".*javascript\\s*:.*");
            
            if (containsScriptTag || containsEventHandler || containsJavaScriptUrl) {
                log.warn("检测到可能的XSS攻击尝试! 清理类型: {}", cleanType);
                // 不记录完整内容，只记录截断的部分，避免日志中出现恶意代码
                String truncated = original.length() > 50 ? original.substring(0, 50) + "..." : original;
                log.warn("可疑内容前缀: {}", truncated.replaceAll("\n", " "));
            } else {
                log.info("HTML内容已被清理，清理类型: {}", cleanType);
            }
        }
    }
} 
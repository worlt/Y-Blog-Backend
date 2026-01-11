package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 补全请求 DTO
 * 
 * @author worlt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiCompletionDTO {

    /**
     * 编辑器内容（上下文）
     */
    private String content;

    /**
     * 博客标题（可选，用于辅助上下文）
     */
    private String title;

    /**
     * 光标前文本（可选，更精确的上下文控制）
     */
    private String textBeforeCursor;

    /**
     * 光标后文本（可选）
     */
    private String textAfterCursor;
}

package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 补全响应 VO
 * 
 * @author worlt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiCompletionVO {

    /**
     * 补全建议文本
     */
    private String completion;

    /**
     * 响应时间戳
     */
    private Long timestamp;
}

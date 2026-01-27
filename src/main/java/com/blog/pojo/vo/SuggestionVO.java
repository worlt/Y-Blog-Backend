package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionVO implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private Integer status;
    private Long wantCount;
    private Long notWantCount;
    private LocalDateTime createTime;
    // 当前用户的投票类型：0:不想要，1:想要，null:未投票
    private Integer currentUserVoteType;
}

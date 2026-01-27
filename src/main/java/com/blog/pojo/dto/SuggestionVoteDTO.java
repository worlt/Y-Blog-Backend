package com.blog.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SuggestionVoteDTO implements Serializable {
    private Long suggestionId;
    // 0:不想要 1:想要
    private Integer type;
}

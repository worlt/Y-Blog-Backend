package com.blog.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SuggestionAddDTO implements Serializable {
    private String title;
    private String content;
}

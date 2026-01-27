package com.blog.service;

import com.blog.pojo.dto.SuggestionAddDTO;
import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.pojo.dto.SuggestionVoteDTO;
import com.blog.pojo.vo.SuggestionVO;
import com.blog.result.PageResult;

public interface SuggestionService {

    void addSuggestion(SuggestionAddDTO addDTO);

    PageResult list(SuggestionPageQueryDTO queryDTO);

    PageResult list(SuggestionPageQueryDTO queryDTO, boolean isAdmin);

    SuggestionVO getById(Long id);

    void vote(SuggestionVoteDTO voteDTO);

    // Admin
    void updateStatus(Long id, Integer status);

    void delete(Long id);
}

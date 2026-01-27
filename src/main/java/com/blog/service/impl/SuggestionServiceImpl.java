package com.blog.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.blog.context.BaseContext;
import com.blog.mapper.SuggestionMapper;
import com.blog.mapper.SuggestionVoteMapper;
import com.blog.pojo.dto.SuggestionAddDTO;
import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.pojo.dto.SuggestionVoteDTO;
import com.blog.pojo.entity.Suggestion;
import com.blog.pojo.entity.SuggestionVote;
import com.blog.pojo.vo.SuggestionVO;
import com.blog.result.PageResult;
import com.blog.service.SuggestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    @Autowired
    private SuggestionMapper suggestionMapper;

    @Autowired
    private SuggestionVoteMapper suggestionVoteMapper;

    @Override
    public void addSuggestion(SuggestionAddDTO addDTO) {
        Suggestion suggestion = new Suggestion();
        BeanUtils.copyProperties(addDTO, suggestion);
        suggestion.setUserId(BaseContext.getCurrentId());
        suggestion.setCreateTime(LocalDateTime.now());
        suggestion.setUpdateTime(LocalDateTime.now());
        suggestion.setStatus(0); // Default unimplemented
        suggestionMapper.insert(suggestion);
    }

    @Override
    public PageResult list(SuggestionPageQueryDTO queryDTO) {
        return list(queryDTO, false);
    }

    @Override
    public PageResult list(SuggestionPageQueryDTO queryDTO, boolean isAdmin) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Long userId = BaseContext.getCurrentId();
        List<SuggestionVO> list = suggestionMapper.list(queryDTO, userId);

        // Blank Vote Logic: Hide counts if user hasn't voted AND is not admin
        if (!isAdmin) {
            list.forEach(vo -> {
                if (vo.getCurrentUserVoteType() == null) {
                    vo.setWantCount(null);
                    vo.setNotWantCount(null);
                }
            });
        }

        Page<SuggestionVO> p = (Page<SuggestionVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public SuggestionVO getById(Long id) {
        Long userId = BaseContext.getCurrentId();
        SuggestionVO vo = suggestionMapper.getDetailById(id, userId);
        if (vo != null && vo.getCurrentUserVoteType() == null) {
            vo.setWantCount(null);
            vo.setNotWantCount(null);
        }
        return vo;
    }

    @Override
    public void vote(SuggestionVoteDTO voteDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }

        // 1. Check Suggestion Status
        Suggestion suggestion = suggestionMapper.getById(voteDTO.getSuggestionId()); // Need to make sure getById exists
                                                                                     // in mapper or use getDetailById
        if (suggestion == null) {
            throw new RuntimeException("建议不存在");
        }
        if (suggestion.getStatus() != 0) {
            throw new RuntimeException("该建议已结束投票");
        }

        // 2. Check Existing Vote (Locking)
        SuggestionVote existingVote = suggestionVoteMapper.getByUserIdAndSuggestionId(userId,
                voteDTO.getSuggestionId());

        if (existingVote != null) {
            throw new RuntimeException("您已投票，不可修改");
        }

        // New vote
        SuggestionVote vote = SuggestionVote.builder()
                .userId(userId)
                .suggestionId(voteDTO.getSuggestionId())
                .type(voteDTO.getType())
                .createTime(LocalDateTime.now())
                .build();
        suggestionVoteMapper.insert(vote);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Suggestion suggestion = Suggestion.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .build();
        suggestionMapper.update(suggestion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除关联投票
        suggestionVoteMapper.deleteBySuggestionId(id);
        // 删除建议
        suggestionMapper.deleteById(id);
    }
}

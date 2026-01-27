package com.blog.mapper;

import com.blog.pojo.entity.SuggestionVote;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SuggestionVoteMapper {

    void insert(SuggestionVote vote);

    @Select("select * from suggestion_vote where user_id = #{userId} and suggestion_id = #{suggestionId}")
    SuggestionVote getByUserIdAndSuggestionId(Long userId, Long suggestionId);

    void update(SuggestionVote vote);

    @Delete("delete from suggestion_vote where suggestion_id = #{suggestionId}")
    void deleteBySuggestionId(Long suggestionId);
}

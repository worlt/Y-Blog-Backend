package com.blog.mapper;

import com.blog.pojo.dto.SuggestionPageQueryDTO;
import com.blog.pojo.entity.Suggestion;
import com.blog.pojo.vo.SuggestionVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SuggestionMapper {

    void insert(Suggestion suggestion);

    void update(Suggestion suggestion);

    @Select("select * from suggestion where id = #{id}")
    Suggestion getById(Long id);

    List<SuggestionVO> list(@Param("query") SuggestionPageQueryDTO queryDTO, @Param("userId") Long userId);

    SuggestionVO getDetailById(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("delete from suggestion where id = #{id}")
    void deleteById(Long id);
}

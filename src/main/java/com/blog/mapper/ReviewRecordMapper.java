package com.blog.mapper;

import com.blog.pojo.dto.ReviewRecordPageQueryDTO;
import com.blog.pojo.entity.ReviewRecord;
import com.blog.pojo.vo.ReviewRecordVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 审核记录Mapper
 * @Author worlt
 * @Date 2025/6/4 下午9:55
 */
@Mapper
public interface ReviewRecordMapper {

    /**
     * 新增审核记录
     * @param reviewRecord
     */
    @Insert("insert into review_record(reviewer_id, blog_id, user_id, comment_id, review_time, review_result, review_reason, message_to_author, review_type) " +
            "values (#{reviewerId}, #{blogId}, #{userId}, #{commentId}, #{reviewTime}, #{reviewResult}, #{reviewReason}, #{messageToAuthor}, #{reviewType})")
    void add(ReviewRecord reviewRecord);

    /**
     * 分页查询审核记录
     * @param reviewRecordPageQueryDTO
     * @return
     */
    Page<ReviewRecordVO> pageQuery(ReviewRecordPageQueryDTO reviewRecordPageQueryDTO);

    /**
     * 根据id查询审核记录详情
     * @param id
     * @return
     */
    ReviewRecordVO getById(Long id);
}

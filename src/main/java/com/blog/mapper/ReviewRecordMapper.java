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
    @Insert("insert into review_record(reviewer_id, blog_id, user_id, review_time, review_result, review_reason, message_to_author) " +
            "values (#{reviewId}, #{blogId}, #{userId}, #{reviewTime}, #{reviewResult}, #{reviewResean}, #{messageToAuthor})")
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

    /**
     * 根据审核类型和目标ID查询审核记录
     * @param reviewType 审核类型
     * @param targetId 目标ID（用户ID、博客ID、评论ID）
     * @return
     */
    Page<ReviewRecordVO> pageQueryByTypeAndTargetId(@Param("reviewType") Integer reviewType, 
                                                  @Param("targetId") Long targetId);

    /**
     * 根据审核员ID查询审核记录
     * @param reviewerId 审核员ID
     * @return
     */
    Page<ReviewRecordVO> pageQueryByReviewerId(@Param("reviewerId") Long reviewerId);
}

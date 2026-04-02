package com.blog.mapper;

import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.entity.CommentReport;
import com.blog.pojo.vo.CommentReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评论举报Mapper
 * 
 * @Author worlt
 * @Date 2025/6/4 下午6:05
 */
@Mapper
public interface CommentReportMapper {

        /**
         * 分页查询
         * 
         * @param commentReportPageQueryDTO
         * @return
         */
        Page<CommentReportVO> pageQuery(CommentReportPageQueryDTO commentReportPageQueryDTO);

        /**
         * 根据id查询举报详情
         * 
         * @param id
         * @return
         */
        CommentReportVO getById(Long id);

        /**
         * 更新举报记录
         * 
         * @param commentReport
         */
        void update(CommentReport commentReport);

        /**
         * 根据id查询举报
         * 
         * @param id
         * @return
         */
        CommentReport selectById(Long id);

        /**
         * 根据id删除举报
         * 
         * @param id
         */
        void deleteById(Long id);

        /**
         * 批量删除举报
         * 
         * @param ids
         */
        void deleteByIds(Long[] ids);

        /**
         * 用户举报评论
         * 
         * @param commentReport
         */
        void add(CommentReport commentReport);

        /**
         * 批量更新同一评论的所有举报状态和审核员
         * 
         * @param commentId  评论ID
         * @param status     状态
         * @param reviewerId 审核员ID
         */
        int updateStatusAndReviewerByCommentId(@Param("commentId") Long commentId,
                        @Param("status") Integer status,
                        @Param("reviewerId") Long reviewerId);

        /**
         * 批量更新同一评论的所有待处理/处理中举报（处理完成时使用）
         *
         * @param commentId    评论ID
         * @param reviewerId   审核员ID
         * @param reviewTime   审核时间
         * @param reviewResult 审核结果
         * @param reviewReason 审核原因
         * @param status       状态
         */
        void batchUpdateByCommentId(@Param("commentId") Long commentId,
                        @Param("reviewerId") Long reviewerId,
                        @Param("reviewTime") java.time.LocalDateTime reviewTime,
                        @Param("reviewResult") Integer reviewResult,
                        @Param("reviewReason") String reviewReason,
                        @Param("status") Integer status);
}
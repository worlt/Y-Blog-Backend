package com.blog.mapper;

import com.blog.pojo.dto.UserReportPageQueryDTO;
import com.blog.pojo.entity.UserReport;
import com.blog.pojo.vo.UserReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户举报Mapper
 * 
 * @Author worlt
 * @Date 2025/6/4 下午2:35
 */
@Mapper
public interface UserReportMapper {

        /**
         * 分页查询
         * 
         * @param userReportPageQueryDTO
         * @return
         */
        Page<UserReportVO> pageQuery(UserReportPageQueryDTO userReportPageQueryDTO);

        /**
         * 根据id查询举报详情
         * 
         * @param id
         * @return
         */
        UserReportVO getById(Long id);

        /**
         * 更新举报记录
         * 
         * @param userReport
         */
        void update(UserReport userReport);

        /**
         * 根据id查询举报
         * 
         * @param id
         * @return
         */
        UserReport selectById(Long id);

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
         * 举报用户
         * 
         * @param userReport
         */
        void add(UserReport userReport);

        /**
         * 批量更新同一用户的所有举报状态和审核员
         * 
         * @param reportedUserId 被举报用户ID
         * @param status         状态
         * @param reviewerId     审核员ID
         */
        int updateStatusAndReviewerByReportedUserId(@Param("reportedUserId") Long reportedUserId,
                        @Param("status") Integer status,
                        @Param("reviewerId") Long reviewerId);

        /**
         * 批量更新同一被举报用户的所有待处理/处理中举报（处理完成时使用）
         *
         * @param reportedUserId 被举报用户ID
         * @param reviewerId     审核员ID
         * @param reviewTime     审核时间
         * @param reviewResult   审核结果
         * @param reviewReason   审核原因
         * @param status         状态
         */
        void batchUpdateByReportedUserId(@Param("reportedUserId") Long reportedUserId,
                        @Param("reviewerId") Long reviewerId,
                        @Param("reviewTime") java.time.LocalDateTime reviewTime,
                        @Param("reviewResult") Integer reviewResult,
                        @Param("reviewReason") String reviewReason,
                        @Param("status") Integer status);
}

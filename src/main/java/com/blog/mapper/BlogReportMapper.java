package com.blog.mapper;

import com.blog.pojo.dto.BlogReportPageQueryDTO;
import com.blog.pojo.entity.BlogReport;
import com.blog.pojo.vo.BlogReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 博客举报Mapper
 * 
 * @Author worlt
 * @Date 2025/6/3 下午6:05
 */
@Mapper
public interface BlogReportMapper {

        /**
         * 分页查询
         * 
         * @param blogReportPageQueryDTO
         * @return
         */
        Page<BlogReportVO> pageQuery(BlogReportPageQueryDTO blogReportPageQueryDTO);

        /**
         * 根据id查询举报详情
         * 
         * @param id
         * @return
         */
        BlogReportVO getById(Long id);

        /**
         * 更新举报记录
         * 
         * @param blogReport
         */
        void update(BlogReport blogReport);

        /**
         * 根据id查询举报
         * 
         * @param id
         * @return
         */
        BlogReport selectById(Long id);

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
         * 新增博客举报
         * 
         * @param blogAddReportDTO
         */
        void add(BlogReport blogAddReportDTO);

        /**
         * 批量更新同一博客的所有待处理/处理中举报（标记为处理中时使用）
         * 
         * @param blogId     博客ID
         * @param status     状态
         * @param reviewerId 审核员ID
         */
        void updateStatusAndReviewerByBlogId(@Param("blogId") Long blogId,
                        @Param("status") Integer status,
                        @Param("reviewerId") Long reviewerId);

        /**
         * 批量更新同一博客的所有待处理/处理中举报（处理完成时使用）
         * 
         * @param blogId       博客ID
         * @param reviewerId   审核员ID
         * @param reviewTime   审核时间
         * @param reviewResult 审核结果
         * @param reviewReason 审核原因
         * @param status       状态
         */
        void batchUpdateByBlogId(@Param("blogId") Long blogId,
                        @Param("reviewerId") Long reviewerId,
                        @Param("reviewTime") java.time.LocalDateTime reviewTime,
                        @Param("reviewResult") Integer reviewResult,
                        @Param("reviewReason") String reviewReason,
                        @Param("status") Integer status);
}

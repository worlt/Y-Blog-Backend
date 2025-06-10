package com.blog.mapper;

import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.entity.CommentReport;
import com.blog.pojo.vo.CommentReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评论举报Mapper
 * @Author worlt
 * @Date 2025/6/4 下午6:05
 */
@Mapper
public interface CommentReportMapper {
    
    /**
     * 分页查询
     * @param commentReportPageQueryDTO
     * @return
     */
    Page<CommentReportVO> pageQuery(CommentReportPageQueryDTO commentReportPageQueryDTO);
    
    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    CommentReportVO getById(Long id);
    
    /**
     * 更新举报记录
     * @param commentReport
     */
    void update(CommentReport commentReport);
    
    /**
     * 根据id查询举报
     * @param id
     * @return
     */
    CommentReport selectById(Long id);
    
    /**
     * 根据id删除举报
     * @param id
     */
    void deleteById(Long id);
    
    /**
     * 批量删除举报
     * @param ids
     */
    void deleteByIds(Long[] ids);

    /**
     * 用户举报评论
     * @param commentReport
     */
    void add(CommentReport commentReport);
}
package com.blog.mapper;

import com.blog.pojo.dto.UserReportPageQueryDTO;
import com.blog.pojo.entity.UserReport;
import com.blog.pojo.vo.UserReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户举报Mapper
 * @Author worlt
 * @Date 2025/6/4 下午2:35
 */
@Mapper
public interface UserReportMapper {
    
    /**
     * 分页查询
     * @param userReportPageQueryDTO
     * @return
     */
    Page<UserReportVO> pageQuery(UserReportPageQueryDTO userReportPageQueryDTO);
    
    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    UserReportVO getById(Long id);
    
    /**
     * 更新举报记录
     * @param userReport
     */
    void update(UserReport userReport);
    
    /**
     * 根据id查询举报
     * @param id
     * @return
     */
    UserReport selectById(Long id);
    
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
}

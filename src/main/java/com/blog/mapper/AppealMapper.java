package com.blog.mapper;

import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.entity.Appeal;
import com.blog.pojo.vo.AppealVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 申诉Mapper
 * @Author worlt
 * @Date 2025/6/4 下午9:22
 */
@Mapper
public interface AppealMapper {
    
    /**
     * 分页查询
     * @param appealPageQueryDTO
     * @return
     */
    Page<AppealVO> pageQuery(AppealPageQueryDTO appealPageQueryDTO);
    
    /**
     * 根据id查询申诉详情
     * @param id
     * @return
     */
    AppealVO getById(Long id);
    
    /**
     * 更新申诉记录
     * @param appeal
     */
    void update(Appeal appeal);
    
    /**
     * 根据id查询申诉
     * @param id
     * @return
     */
    Appeal selectById(Long id);
    
    /**
     * 根据id删除申诉
     * @param id
     */
    void deleteById(Long id);
    
    /**
     * 批量删除申诉
     * @param ids
     */
    void deleteByIds(Long[] ids);
    
    /**
     * 添加申诉
     * @param appeal
     */
    void add(Appeal appeal);
} 
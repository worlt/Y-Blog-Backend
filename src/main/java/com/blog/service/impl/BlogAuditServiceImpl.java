package com.blog.service.impl;

import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.BlogAuditVO;
import com.blog.result.PageResult;
import com.blog.service.BlogAuditService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:23
 */
@Service
@Slf4j
public class BlogAuditServiceImpl implements BlogAuditService {

    @Autowired
    private BlogAuditMapper blogAuditMapper;
    @Autowired
    private BlogTagMapper blogTagMapper;
    @Autowired
    private BlogCustomTagMapper blogCustomTagMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogMapper blogMapper;

    /**
     * 分页查询审核博客
     * @param blogAuditPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(BlogAuditPageQueryDTO blogAuditPageQueryDTO) {
        blogAuditPageQueryDTO.setStatus(BlogStatusConstant.PENDING);
        PageHelper.startPage(blogAuditPageQueryDTO.getPage(), blogAuditPageQueryDTO.getPageSize());
        Page<BlogAuditVO> page = blogAuditMapper.pageQuery(blogAuditPageQueryDTO);

        // 拼接 tageList 和customTagList
        List<BlogAuditVO> blogAuditVOList = page.getResult();

        if (!blogAuditVOList.isEmpty()) {
            // 搜集所有审核博客id
            List<Long> blogAuditIds = blogAuditVOList.stream().map(BlogAuditVO::getId).collect(Collectors.toList());

            // 一次性获取所有博客标签
            Map<Long, List<String>> blogTagMap = blogTagMapper.getTagsByBlogIds(blogAuditIds);
            Map<Long, List<String>> blogCustomTag = blogCustomTagMapper.getCustomTagsByBlogIds(blogAuditIds);

            // 组装所有数据
            for (BlogAuditVO blogAuditVO : blogAuditVOList) {
                Map<String, List<String>> tagListMap = (Map<String, List<String>>) blogTagMap.get(blogAuditVO.getId());
                List<String> tagList = tagListMap != null ? tagListMap.get("value") : Collections.emptyList();
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTag.get(blogAuditVO.getId());
                List<String> customTagList = customTagListMap != null ? customTagListMap.get("value") : Collections.emptyList();

                // 设置标签到blogAuditVO
                blogAuditVO.setTagList(tagList != null ? tagList : Collections.emptyList());
                blogAuditVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());
            }
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询审核博客详情
     * @param id
     * @return
     */
    @Override
    public BlogAuditVO getById(Long id) {
        BlogAuditVO blogAuditVO = blogAuditMapper.getById(id);
        blogAuditVO.setUsername(userMapper.getById(blogAuditVO.getUserId()).getUsername());
        blogAuditVO.setCategory(categoryMapper.getById(blogAuditVO.getCategoryId()).getName());
        List<String> tagList = blogTagMapper.getByBlogId(id);
        List<String> customTagList = blogCustomTagMapper.getByBlogId(id);
        blogAuditVO.setTagList(tagList);
        blogAuditVO.setCustomTagList(customTagList);
        return blogAuditVO;
    }

    /**
     * 审核博客
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        blogMapper.updateStatus(id, status);
    }
}

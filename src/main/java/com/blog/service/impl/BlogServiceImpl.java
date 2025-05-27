package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;
import com.blog.service.BlogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:13
 */
@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogTagMapper blogTagMapper;
    @Autowired
    private BlogCustomTagMapper blogCustomTagMapper;

    /**
     * 分页查询博客文章
     * @param blogPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(BlogPageQueryDTO blogPageQueryDTO) {
        PageHelper.startPage(blogPageQueryDTO.getPage(), blogPageQueryDTO.getPageSize());
        Page<BlogVO> page = blogMapper.page(blogPageQueryDTO);
        Long total = page.getTotal();
        List<BlogVO> blogVOList = page.getResult();

        if (!blogVOList.isEmpty()) {
            // 收集所有博客id
            List<Long> blogIds = blogVOList.stream().map(BlogVO::getId).collect(Collectors.toList());

            // 一次性获取所有博客的标签
            Map<Long, List<String>> blogTagMap = blogTagMapper.getTagsByBlogIds(blogIds);
            Map<Long, List<String>> blogCustomTagMap = blogCustomTagMapper.getCustomTagsByBlogIds(blogIds);
//            log.info("blogTagMap：{}", blogTagMap);
//            log.info("blogCustomTagMap：{}", blogCustomTagMap);

            // 组装数据
//            for (BlogVO blogVO : blogVOList) {
//                blogVO.setTagList(blogTagMap.getOrDefault(blogVO.getId(), Collections.emptyList()));
//                blogVO.setCustomTagList(blogCustomTagMap.getOrDefault(blogVO.getId(), Collections.emptyList()));
//            }

//            Map<String, List<String>> listMap = (Map<String, List<String>>) blogTagMap.get(1L);
//            List<String> list = listMap.get("value");
//            System.out.println("list:"+ list);

            // 组装数据
            for (BlogVO blogVO : blogVOList) {
//                log.info("blog_id:{}",blogVO.getId());
                Map<String, List<String>> tagListMap = (Map<String, List<String>>) blogTagMap.get(blogVO.getId());
//                log.info("tagListMap:{}", tagListMap);
                List<String> tagList = tagListMap.get("value");
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTagMap.get(blogVO.getId());
//                log.info("customTagListMap:{}", customTagListMap);
                List<String> customTagList = customTagListMap.get("value");

                // 设置标签列表到blogVO对象
                blogVO.setTagList(tagList);
                blogVO.setCustomTagList(customTagList);
            }

        }

        return new PageResult(total,blogVOList);
    }

    /**
     * 新增博客文章
     * @param blogAddDTO
     */
    @Override
    @Transactional
    public void add(BlogAddDTO blogAddDTO) {

    }
}

package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.BlogCustomTag;
import com.blog.pojo.entity.BlogTag;
import com.blog.pojo.entity.Tag;
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
import com.blog.utils.XssCleanUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                Map<String, List<String>> tagListMap = (Map<String, List<String>>) blogTagMap.get(blogVO.getId());
                List<String> tagList = tagListMap.get("value");
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTagMap.get(blogVO.getId());
                List<String> customTagList = customTagListMap.get("value");
//                log.info("blog_id:{}",blogVO.getId());
//                log.info("tagListMap:{}", tagListMap);
//                log.info("customTagListMap:{}", customTagListMap);

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
        // 应用XSS清理
        blogAddDTO.setTitle(XssCleanUtil.cleanAllTags(blogAddDTO.getTitle()));
        blogAddDTO.setContent(XssCleanUtil.cleanRichText(blogAddDTO.getContent()));

        // 检测到恶意脚本，清理后如果为空
        if (blogAddDTO.getTitle().isEmpty()) throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogAddDTO.getContent().isEmpty()) throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

        // 复制属性
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogAddDTO, blog);

        if (userMapper.getById(blog.getUserId()) == null) throw new RuntimeException(MessageConstant.ACCOUNT_NOT_FOUND);
        if (categoryMapper.getById(blog.getCategoryId()) == null) throw new RuntimeException(MessageConstant.CATEGORY_NOT_FOUND);

        // 设置默认值
        blog.setStatus(BlogStatusConstant.PENDING);
        blog.setViews(0L);
        blog.setLikes(0L);
        blog.setDislikes(0L);
        blog.setCollections(0L);
        blog.setShares(0L);
        blog.setPublishTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());

        // 插入博客
        blogMapper.add(blog);
        // 获取插入博客的id
        Long id = blog.getId();

        // 构造tag
        if (!blogAddDTO.getTagIdList().isEmpty()) {
            List<BlogTag> blogTags = new ArrayList<>();

            for (Long tagId : blogAddDTO.getTagIdList()) {
                BlogTag blogTag = BlogTag.builder()
                        .blogId(id)
                        .tagId(tagId)
                        .createTime(LocalDateTime.now())
                        .build();
                blogTags.add(blogTag);
            }

            if (!blogTags.isEmpty()) {
                blogTagMapper.insertBatch(blogTags);
            }

        }

        // 构造customTag
        if (!blogAddDTO.getCustomTagList().isEmpty()) {
            List<BlogCustomTag> blogCustomTags = new ArrayList<>();
            for (String customTag : blogAddDTO.getCustomTagList()) {
                BlogCustomTag blogCustomTag = BlogCustomTag.builder()
                        .blogId(id)
                        .customTag(customTag)
                        .createTime(LocalDateTime.now())
                        .build();
                blogCustomTags.add(blogCustomTag);
            }

            if (!blogCustomTags.isEmpty()) {
                blogCustomTagMapper.insertBatch(blogCustomTags);
            }
        }
    }

    /**
     * 编辑博客
     * @param blogUpdateDTO
     */
    @Override
    @Transactional
    public void update(BlogUpdateDTO blogUpdateDTO) {
        // 清理XSS
        blogUpdateDTO.setTitle(XssCleanUtil.cleanAllTags(blogUpdateDTO.getTitle()));
        blogUpdateDTO.setContent(XssCleanUtil.cleanRichText(blogUpdateDTO.getContent()));

        // 清理结果
        if (blogUpdateDTO.getTitle().isEmpty()) throw new RuntimeException(MessageConstant.TITLE_IS_BLANK);
        if (blogUpdateDTO.getContent().isEmpty()) throw new RuntimeException(MessageConstant.CONTENT_IS_BLANK);

        Blog blog = new Blog();
        BeanUtils.copyProperties(blogUpdateDTO, blog);

        // 删除所有标签
        blogTagMapper.deleteByBlogId(blog.getId());
        blogCustomTagMapper.deleteByBlogId(blog.getId());

        if (!blogUpdateDTO.getTagIdList().isEmpty()) {
            List<BlogTag> blogTags = new ArrayList<>();
            for (Long tagId : blogUpdateDTO.getTagIdList()) {
                BlogTag blogTag = BlogTag.builder()
                        .blogId(blog.getId())
                        .tagId(tagId)
                        .build();
                blogTags.add(blogTag);
            }
            blogTagMapper.insertBatch(blogTags);
        }

        if (!blogUpdateDTO.getCustomTagList().isEmpty()) {
            List<BlogCustomTag> blogCustomTags = new ArrayList<>();
            for (String customTag : blogUpdateDTO.getCustomTagList()) {
                BlogCustomTag blogCustomTag = BlogCustomTag.builder()
                        .blogId(blog.getId())
                        .customTag(customTag)
                        .build();
                blogCustomTags.add(blogCustomTag);
            }
            blogCustomTagMapper.insertBatch(blogCustomTags);
        }
    }
}

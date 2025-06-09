package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.BlogTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.pojo.dto.TagDTO;
import com.blog.pojo.entity.Tag;
import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签管理服务实现类
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private BlogTagMapper blogTagMapper;
    
    /**
     * 添加标签
     * 
     * @param tagDTO 标签数据传输对象
     * @throws RuntimeException 当标签名已存在或添加失败时抛出
     */
    @Override
    @Transactional
    public void add(TagDTO tagDTO) {
        // 1. 检查标签名称是否已存在
        Tag existingTag = tagMapper.getByName(tagDTO.getName());
        if (existingTag != null) {
            throw new RuntimeException(MessageConstant.TAG_ALREADY_EXISTS);
        }
        
        // 2. 创建标签
        Tag tag = Tag.builder()
                .name(tagDTO.getName())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
                
        int rows = tagMapper.insert(tag);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.TAG_ADD_FAIL);
        }
        
        log.info("新增标签成功: {}", tag.getName());
    }

    /**
     * 更新标签信息
     * 
     * @param tagDTO 标签数据传输对象
     * @throws RuntimeException 当标签不存在、名称已被其他标签使用或更新失败时抛出
     */
    @Override
    @Transactional
    public void update(TagDTO tagDTO) {
        // 1. 检查标签是否存在
        Tag existingTag = tagMapper.getById(tagDTO.getId());
        if (existingTag == null) {
            throw new RuntimeException(MessageConstant.TAG_NOT_FOUND);
        }
        
        // 2. 检查是否有同名标签（非当前ID）
        Tag tagByName = tagMapper.getByName(tagDTO.getName());
        if (tagByName != null && !tagByName.getId().equals(tagDTO.getId())) {
            throw new RuntimeException(MessageConstant.TAG_ALREADY_EXISTS);
        }
        
        // 3. 更新标签
        existingTag.setName(tagDTO.getName());
        existingTag.setUpdateTime(LocalDateTime.now());
        
        int rows = tagMapper.update(existingTag);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.UPDATEException);
        }
        
        log.info("更新标签成功: {}", existingTag.getName());
    }

    /**
     * 删除标签
     * 删除前会先处理与博客的关联关系
     * 
     * @param id 标签ID
     * @throws RuntimeException 当标签不存在或删除失败时抛出
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 1. 检查标签是否存在
        Tag tag = tagMapper.getById(id);
        if (tag == null) {
            throw new RuntimeException(MessageConstant.TAG_NOT_FOUND);
        }
        
        // 2. 检查是否有博客使用该标签
        Integer count = blogTagMapper.countByTagId(id);
        if (count != null && count > 0) {
            // 先删除博客与标签的关联关系
            blogTagMapper.deleteByTagId(id);
        }
        
        // 3. 删除标签
        int rows = tagMapper.deleteById(id);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.TAG_DELETE_FAIL);
        }
        
        log.info("删除标签成功: {}", tag.getName());
    }

    /**
     * 根据ID获取标签详情
     * 
     * @param id 标签ID
     * @return 标签对象
     * @throws RuntimeException 当标签不存在时抛出
     */
    @Override
    public Tag getById(Long id) {
        Tag tag = tagMapper.getById(id);
        if (tag == null) {
            throw new RuntimeException(MessageConstant.TAG_NOT_FOUND);
        }
        return tag;
    }

    /**
     * 获取所有标签列表
     * 
     * @return 标签对象列表
     */
    @Override
    public List<Tag> listAll() {
        return tagMapper.listAll();
    }
} 
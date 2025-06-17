package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.dto.BlogAuditorDTO;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Conversation;
import com.blog.pojo.entity.Message;
import com.blog.pojo.entity.ReviewRecord;
import com.blog.pojo.vo.BlogAuditVO;
import com.blog.result.PageResult;
import com.blog.service.BlogAuditService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Autowired
    private ReviewRecordMapper reviewRecordMapper;
    @Autowired
    private ConversationMapper conversationMapper;
    @Autowired
    private MessageMapper messageMapper;

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
        List<String> tagList = blogTagMapper.getByBlogId(id) !=null ? blogTagMapper.getByBlogId(id) : Collections.emptyList();
        List<String> customTagList = blogCustomTagMapper.getByBlogId(id) != null ? blogCustomTagMapper.getByBlogId(id) : Collections.emptyList();
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

    /**
     * 处理博客审核
     * @param blogAuditorDTO
     */
    @Override
    @Transactional
    public void handle(BlogAuditorDTO blogAuditorDTO) {
        blogAuditorDTO.setReviewerId(BaseContext.getCurrentId());
        if (blogAuditorDTO.getStatus().equals(BlogStatusConstant.PENDING)) throw new RuntimeException("审核时不能设置为待审核");
        Blog blog = blogMapper.getById(blogAuditorDTO.getBlogId());
        if (blog == null) throw new RuntimeException("博客不存在");

        // 构建消息
        String messageContent = null;

        if (blogAuditorDTO.getStatus().equals(BlogStatusConstant.APPROVED)) {
            messageContent = "您的博客《" + blogAuditorDTO.getBlogTitle() + "》已通过审核。" + blogAuditorDTO.getMessage();
        } else {
            if (blogAuditorDTO.getMessage() == null || blogAuditorDTO.getMessage().isEmpty()) {
                throw new RuntimeException("messageContent 必填，当前博客状态：" + blogAuditorDTO.getStatus());
            }
            if (blogAuditorDTO.getStatus().equals(BlogStatusConstant.REJECTED) || blogAuditorDTO.getStatus().equals(BlogStatusConstant.IN_REVIEW)) {
                messageContent = "您的博客《" + blogAuditorDTO.getBlogTitle() + "》未通过审核，请修改后重新提交。" + blogAuditorDTO.getMessage();
            } else if (blogAuditorDTO.getStatus().equals(BlogStatusConstant.PERMANENTLY_BANNED)) {
                messageContent = "您的博客《" + blogAuditorDTO.getBlogTitle() + "》已被永久封禁。" + blogAuditorDTO.getMessage();
            }
        }

        // 创建审核记录
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(blogAuditorDTO.getReviewerId())
                .blogId(blogAuditorDTO.getBlogId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(blogAuditorDTO.getStatus())
                .reviewReason(blogAuditorDTO.getMessage())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.BLOG)
                .build();
        reviewRecordMapper.add(reviewRecord);

        // 发送消息通知
        Long receiverId = blog.getUserId();

        // 查找是否存在会话，不存在则创建新的会话
        Long systemId = 0L;
        Long conversationId;
        Conversation conversation = conversationMapper.findByUserPair(systemId, receiverId);

        if (conversation == null) {
            // 创建新会话
            conversation = Conversation.builder()
                    .user1Id(systemId)
                    .user2Id(receiverId)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .lastMessage(messageContent)
                    .unreadCount1(0)
                    .unreadCount2(1) // 接收方未读消息+1
                    .type(ConversationTypeConstant.SYSTEM)
                    .build();
            conversationMapper.insert(conversation);
            conversationId = conversation.getId();
        } else {
            conversationId = conversation.getId();
            // 更新会话最后消息和未读计数
            conversation.setLastMessage(messageContent);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateLastMessage(conversation);
            conversationMapper.incrementUnreadCount2(conversationId);
        }

        // 创建消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(systemId)
                .receiverId(receiverId)
                .content(messageContent)
                .sendTime(LocalDateTime.now())
                .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                .messageType(MessageTypeConstant.SYSTEM)
                .build();
        messageMapper.add(message);

        // 更新博客状态
        blogMapper.updateStatus(blogAuditorDTO.getBlogId(), blogAuditorDTO.getStatus());
    }
}

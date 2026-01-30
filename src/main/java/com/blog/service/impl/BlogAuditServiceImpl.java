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
    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * 分页查询审核博客
     * 
     * @param blogAuditPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(BlogAuditPageQueryDTO blogAuditPageQueryDTO) {

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
                Map<String, List<String>> customTagListMap = (Map<String, List<String>>) blogCustomTag
                        .get(blogAuditVO.getId());
                List<String> customTagList = customTagListMap != null ? customTagListMap.get("value")
                        : Collections.emptyList();

                // 设置标签到blogAuditVO
                blogAuditVO.setTagList(tagList != null ? tagList : Collections.emptyList());
                blogAuditVO.setCustomTagList(customTagList != null ? customTagList : Collections.emptyList());
            }
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询审核博客详情
     * 
     * @param id
     * @return
     */
    @Override
    public BlogAuditVO getById(Long id) {
        BlogAuditVO blogAuditVO = blogAuditMapper.getById(id);
        blogAuditVO.setUsername(userMapper.getById(blogAuditVO.getUserId()).getUsername());
        blogAuditVO.setCategory(categoryMapper.getById(blogAuditVO.getCategoryId()).getName());
        List<String> tagList = blogTagMapper.getByBlogId(id) != null ? blogTagMapper.getByBlogId(id)
                : Collections.emptyList();
        List<String> customTagList = blogCustomTagMapper.getByBlogId(id) != null ? blogCustomTagMapper.getByBlogId(id)
                : Collections.emptyList();
        blogAuditVO.setTagList(tagList);
        blogAuditVO.setCustomTagList(customTagList);
        return blogAuditVO;
    }

    /**
     * 审核博客
     * 
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        blogMapper.updateStatus(id, status);
    }

    /**
     * 处理博客审核
     * 
     * @param blogAuditorDTO
     */
    @Override
    @Transactional
    public void handle(BlogAuditorDTO blogAuditorDTO) {
        blogAuditorDTO.setReviewerId(BaseContext.getCurrentId());
        if (blogAuditorDTO.getStatus().equals(BlogStatusConstant.PENDING))
            throw new RuntimeException("审核时不能设置为待审核");

        Blog blog = blogMapper.getById(blogAuditorDTO.getBlogId());
        if (blog == null)
            throw new RuntimeException("博客不存在");

        // 权限检查：如果博客状态是处理中且有reviewerId，必须是同一个审核员
        if (blog.getStatus() != null && blog.getStatus().equals(BlogStatusConstant.IN_PROCESS)) {
            if (blog.getReviewerId() != null && !blog.getReviewerId().equals(blogAuditorDTO.getReviewerId())) {
                throw new RuntimeException("该博客正在被其他审核员处理，您无权操作");
            }
        }

        // 构建消息内容，格式参考管理端
        String messageContent = buildAuditMessage(blogAuditorDTO.getBlogTitle(),
                blogAuditorDTO.getStatus(), blogAuditorDTO.getMessage());

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

        // WebSocket 实时推送
        try {
            // 构造推送消息结构
            java.util.Map<String, Object> wsMsg = new java.util.HashMap<>();
            wsMsg.put("type", "SYSTEM_MESSAGE");

            // 构造前端需要的消息格式
            com.blog.pojo.vo.MessageVO msgVO = new com.blog.pojo.vo.MessageVO();
            org.springframework.beans.BeanUtils.copyProperties(message, msgVO);
            msgVO.setIsMine(false);
            msgVO.setMessageType(MessageTypeConstant.SYSTEM);
            msgVO.setSenderName("系统通知");
            msgVO.setSenderAvatar("/images/system_avatar.png");

            wsMsg.put("data", msgVO);
            wsMsg.put("conversationId", conversationId);

            String jsonEntry = objectMapper.writeValueAsString(wsMsg);
            com.blog.server.WebSocketServer.sendInfo(jsonEntry, String.valueOf(receiverId));
        } catch (Exception e) {
            log.warn("WebSocket推送审核通知失败: {}", e.getMessage());
        }

        // 更新博客状态
        blogMapper.updateStatus(blogAuditorDTO.getBlogId(), blogAuditorDTO.getStatus());
    }

    /**
     * 构建审核消息内容（与管理端格式一致）
     * 
     * @param blogTitle 博客标题
     * @param status    审核状态
     * @param reason    审核原因/意见
     * @return 消息内容
     */
    private String buildAuditMessage(String blogTitle, Integer status, String reason) {
        // 非通过状态必须填写原因
        if (!status.equals(BlogStatusConstant.APPROVED)
                && (reason == null || reason.trim().isEmpty())) {
            throw new RuntimeException("审核意见为必填项");
        }

        String baseMessage;
        switch (status) {
            case 1: // 已通过 (BlogStatusConstant.APPROVED)
                return String.format("您的博客《%s》审核已通过，现已发布。", blogTitle);
            case 2: // 已拒绝 (BlogStatusConstant.REJECTED)
                baseMessage = String.format("您的博客《%s》审核未通过。", blogTitle);
                break;
            case 3: // 整改中 (BlogStatusConstant.IN_REVIEW)
                baseMessage = String.format("您的博客《%s》需要整改。", blogTitle);
                break;
            case 4: // 已封禁 (BlogStatusConstant.PERMANENTLY_BANNED)
                baseMessage = String.format("您的博客《%s》因违反社区规范已被封禁。如有异议，请联系管理员申诉。", blogTitle);
                break;
            default:
                throw new RuntimeException("不支持的审核状态：" + status);
        }

        // 附加原因说明
        if (reason != null && !reason.trim().isEmpty()) {
            return baseMessage + "原因：" + reason.trim();
        }
        return baseMessage;
    }

    /**
     * 标记为处理中（联动更新同一作者的所有待审核博客）
     * 
     * @param id         博客ID
     * @param reviewerId 审核员ID
     */
    @Override
    @Transactional
    public void markAsInProcess(Long id, Long reviewerId) {
        log.info("标记博客审核为处理中：id={}, reviewerId={}", id, reviewerId);

        // 查询博客信息
        Blog blog = blogMapper.getById(id);
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }

        // 检查博客是否处于待审核状态
        if (blog.getStatus().equals(BlogStatusConstant.IN_REVIEW)) {
            // 如果已在审核中，检查是否为当前审核员
            // 注意：这里需要Blog表有reviewer_id。假设BlogMapper.getById返回的Blog包含reviewerId。
            // 现有的blogMapper.getById可能不包含reviewerId，需要确认。
            // 不过根据之前的需求，Blog表应该没有加reviewerId，而是直接更新状态？
            // 稍等，task.md里提到Blog表需要确认。
            // 如果Blog表没有reviewer_id，那么这个检查无法进行。
            // 但BlogReport有reviewer_id。
            // 这里的业务是"博客审核"，不是"博客举报审核"。
            // 如果Blog表没有reviewer_id，那么无法实现"抢占"检查。
            // 但之前的代码调用了 updateStatusAndReviewerByBlogId，说明Mapper支持更新reviewerId。
            // 说明Blog表确实有reviewer_id (由 add_blog_reviewer_id.sql 添加)。

            if (blog.getReviewerId() != null && !blog.getReviewerId().equals(reviewerId)) {
                throw new RuntimeException("该博客正在被其他审核员审核，无法操作");
            }
            return;
        }

        if (!blog.getStatus().equals(BlogStatusConstant.PENDING)) {
            throw new RuntimeException("该博客不是待审核状态");
        }

        // 批量更新同一作者的所有待审核博客为处理中状态
        blogMapper.updateStatusAndReviewerByUserId(
                blog.getUserId(),
                BlogStatusConstant.IN_PROCESS,
                reviewerId);

        log.info("已将用户ID={}的所有待审核博客标记为处理中，处理人ID={}", blog.getUserId(), reviewerId);
    }
}

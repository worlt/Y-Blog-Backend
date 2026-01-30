package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.ShareMapper;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Share;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.ShareStatisticsVO;
import com.blog.pojo.vo.ShareVO;
import com.blog.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 博客分享服务实现类
 * 管理博客分享相关业务逻辑
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;
    // 缓存服务，用于在分享后清除博客缓存
    @Autowired
    private BlogCacheServiceImpl blogCacheService;

    /**
     * 支持的分享平台列表
     */
    private static final List<String> SUPPORTED_PLATFORMS = Arrays.asList(
            "微信", "微博", "QQ", "知乎");

    /**
     * 分享博客到指定平台
     * 
     * @param blogId   博客ID
     * @param userId   用户ID
     * @param platform 分享平台
     * @throws RuntimeException 当平台不支持、博客不存在或分享失败时抛出
     */
    @Override
    @Transactional
    public void shareBlog(Long blogId, Long userId, String platform) {
        // 1. 检查平台是否支持
        if (platform == null || platform.isEmpty()) {
            throw new RuntimeException(MessageConstant.INVALID_PLATFORM);
        }

        if (!SUPPORTED_PLATFORMS.contains(platform)) {
            throw new RuntimeException(MessageConstant.PLATFORM_NOT_SUPPORTED);
        }

        // 2. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }

        // 3. 创建分享记录
        Share share = Share.builder()
                .userId(userId)
                .blogId(blogId)
                .sharePlatform(platform)
                .shareTime(LocalDateTime.now())
                .build();

        int rows = shareMapper.insert(share);
        log.info("用户{}分享了博客{}到{}", userId, blogId, platform);
        if (rows <= 0) {
            throw new RuntimeException(MessageConstant.SHARE_FAIL);
        }

        // 4. 更新博客分享数量
        updateBlogShareCount(blogId);
    }

    /**
     * 获取博客的分享统计信息
     * 包括总分享次数和各平台分享次数
     * 
     * @param blogId 博客ID
     * @return 分享统计信息VO对象
     * @throws RuntimeException 当博客不存在时抛出
     */
    @Override
    public ShareStatisticsVO getShareStatistics(Long blogId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }

        // 2. 获取总分享次数
        int totalShares = shareMapper.countByBlogId(blogId);

        // 3. 获取各平台分享次数
        Map<String, Integer> platformShares = new HashMap<>();
        for (String platform : SUPPORTED_PLATFORMS) {
            int count = shareMapper.countByBlogIdAndPlatform(blogId, platform);
            if (count > 0) {
                platformShares.put(platform, count);
            }
        }

        // 4. 构建返回对象
        return ShareStatisticsVO.builder()
                .blogId(blogId)
                .totalShares(totalShares)
                .platformShares(platformShares)
                .build();
    }

    /**
     * 获取系统支持的所有分享平台列表
     * 
     * @return 平台名称列表
     */
    @Override
    public List<String> getSupportedPlatforms() {
        return SUPPORTED_PLATFORMS;
    }

    /**
     * 获取指定用户的所有分享记录
     * 
     * @param userId 用户ID
     * @return 分享记录VO对象列表
     */
    @Override
    public List<ShareVO> getUserShares(Long userId) {
        List<Share> shares = shareMapper.findByUserId(userId);
        return convertToShareVOList(shares);
    }

    /**
     * 获取指定博客的所有分享记录
     * 
     * @param blogId 博客ID
     * @return 分享记录VO对象列表
     * @throws RuntimeException 当博客不存在时抛出
     */
    @Override
    public List<ShareVO> getBlogShares(Long blogId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }

        List<Share> shares = shareMapper.findByBlogId(blogId);
        return convertToShareVOList(shares);
    }

    /**
     * 将Share实体列表转换为ShareVO列表
     * 会自动过滤无效的用户和博客
     * 
     * @param shares Share实体列表
     * @return ShareVO列表
     */
    private List<ShareVO> convertToShareVOList(List<Share> shares) {
        List<ShareVO> shareVOS = new ArrayList<>();

        for (Share share : shares) {
            // 获取用户信息
            User user = userMapper.getById(share.getUserId());
            if (user == null) {
                continue;
            }

            // 获取博客信息
            Blog blog = blogMapper.getById(share.getBlogId());
            if (blog == null) {
                continue;
            }

            // 构建VO对象
            ShareVO vo = ShareVO.builder()
                    .id(share.getId())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .avatar(user.getAvatar())
                    .blogId(blog.getId())
                    .blogTitle(blog.getTitle())
                    .shareTime(share.getShareTime())
                    .sharePlatform(share.getSharePlatform())
                    .build();

            shareVOS.add(vo);
        }

        return shareVOS;
    }

    /**
     * 更新博客分享数量并清除缓存
     * 
     * @param blogId 博客ID
     */
    private void updateBlogShareCount(Long blogId) {
        try {
            // 使用增量更新直接增加博客分享计数
            blogMapper.incrementShares(blogId);
            log.info("更新博客{}分享量+1", blogId);

            // 清除博客缓存，确保下次查询时获取最新数据
            blogCacheService.evictBlogCache(blogId);
            log.info("已清除博客{}的缓存", blogId);
        } catch (Exception e) {
            log.error("更新博客分享数量失败", e);
        }
    }
}
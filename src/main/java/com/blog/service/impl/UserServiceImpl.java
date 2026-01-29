package com.blog.service.impl;

import com.blog.constant.PasswordConstant;
import com.blog.constant.RoleConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.LikeMapper;
import com.blog.mapper.UserFollowMapper;
import com.blog.mapper.UserMapper;
import com.blog.mapper.ViewMapper;
import com.blog.pojo.dto.*;
import com.blog.pojo.entity.User;
import com.blog.result.PageResult;
import com.blog.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.blog.service.impl.BlogCacheServiceImpl;
import com.blog.service.impl.AdvancedCacheManagerImpl;
import org.springframework.transaction.annotation.Transactional;
import com.blog.mapper.EmailVerificationMapper;
import com.blog.mapper.EmailVerificationMapper;
import com.blog.service.EmailService;
import com.blog.service.NotificationService;

/**
 * 用户服务实现类
 * 集成了Redis缓存策略，优化查询性能
 * 
 * @author Worlt
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailVerificationMapper emailVerificationMapper;

    @Autowired
    private EmailService emailService;

    // 集成缓存服务
    @Autowired
    private BlogCacheServiceImpl blogCacheService;

    @Autowired
    private AdvancedCacheManagerImpl advancedCacheManager;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private ViewMapper viewMapper;

    /**
     * 用户分页查询
     *
     * @param userPAgeQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPAgeQueryDTO) {
        PageHelper.startPage(userPAgeQueryDTO.getPage(), userPAgeQueryDTO.getPageSize());
        Page<User> page = userMapper.pageQuery(userPAgeQueryDTO);
        Long total = page.getTotal();
        List<User> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 新增用户
     *
     * @param userDTO
     */
    @Override
    public void add(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (user.getStatus() == null)
            user.setStatus(UserStatusConstant.NORMAL);
        if (user.getRole() == null)
            user.setRole(RoleConstant.USER);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        if (user.getFans() == null)
            user.setFans(0L);

        userMapper.add(user);
    }

    /**
     * 编辑用户信息
     *
     * @param userDTO
     */
    @Override
    @Transactional
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (user.getRole() != null
                && (user.getRole().equals(RoleConstant.ADMIN) || user.getRole().equals(RoleConstant.AUDITOR))) {
            throw new RuntimeException("用户不能直接升级为管理员或审核员");
        }

        if (user.getPassword() != null) {
            String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(pwd);
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        evictUserCache(user.getId());
    }

    /**
     * 启用禁用用户（会发送邮件通知）
     *
     * @param id     用户ID
     * @param status 状态
     * @param reason 封禁/解禁原因
     */
    @Override
    public void setStatus(Long id, Integer status, String reason) {
        // 获取用户信息用于发送邮件
        User user = userMapper.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 判断状态是否有变化
        boolean statusChanged = !status.equals(user.getStatus());

        // 更新用户状态
        userMapper.setStatus(id, status);

        // 如果状态有变化，发送通知
        if (statusChanged) {
            boolean isBanned = status.equals(UserStatusConstant.BANNED);

            // 1. 发送邮件
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendBanNotificationEmail(user.getEmail(), user.getUsername(), isBanned, reason);
            }

            // 2. 发送站内信系统通知
            String messageContent;
            String safeReason = reason != null ? reason : "无";
            if (isBanned) {
                messageContent = "您的账号已被封禁。原因：" + safeReason;
            } else {
                messageContent = "您的账号已解禁。原因：" + safeReason;
            }
            // 使用0L作为发送者ID，表示系统通知
            notificationService.sendSystemMessage(id, 0L, messageContent);

            log.info("用户状态变更通知已发送，用户ID：{}，新状态：{}", id, isBanned ? "封禁" : "正常");
        }
    }

    /**
     * 启用禁用用户（无原因，用于兼容旧接口）
     *
     * @param id     用户ID
     * @param status 状态
     */
    @Override
    public void setStatus(Long id, Integer status) {
        setStatus(id, status, null);
    }

    /**
     * 根据id删除用户
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("开始删除用户，用户ID：{}", id);

        try {
            userMapper.deleteById(id);

            // 清除相关缓存
            evictUserCache(id);

            log.info("用户删除成功，用户ID：{}", id);
        } catch (Exception e) {
            log.error("用户删除失败，用户ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new RuntimeException("用户删除失败");
        }
    }

    /**
     * 根据id查询用户（集成缓存）
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public User getById(Long id) {
        log.debug("开始查询用户信息，用户ID：{}", id);

        if (id == null) {
            return null;
        }

        // 1. 先尝试从缓存获取
        try {
            return advancedCacheManager.getWithProtection(
                    "user:" + id,
                    () -> {
                        // 缓存未命中时从数据库加载
                        User user = userMapper.getById(id);
                        if (user != null) {
                            log.debug("从数据库加载用户信息成功，用户ID：{}，用户名：{}", id, user.getUsername());
                        } else {
                            log.debug("用户不存在，用户ID：{}", id);
                        }
                        return user;
                    },
                    60,
                    java.util.concurrent.TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("缓存获取用户信息失败，降级到数据库查询，用户ID：{}", id, e);
            return userMapper.getById(id);
        }
    }

    /**
     * 邮箱验证码注册
     *
     * @param userEmailRegisterDTO 注册信息
     */
    @Override
    public void registerByEmail(UserEmailRegisterDTO userEmailRegisterDTO) {
        // 检查邮箱是否已注册
        User existingUser = userMapper.getByEmail(userEmailRegisterDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 创建新用户
        User user = User.builder()
                .username(userEmailRegisterDTO.getUsername())
                .password(DigestUtils.md5DigestAsHex(userEmailRegisterDTO.getPassword().getBytes()))
                .email(userEmailRegisterDTO.getEmail())
                .role(RoleConstant.USER)
                .status(UserStatusConstant.NORMAL)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .fans(0L)
                .build();

        userMapper.add(user);
    }

    /**
     * 邮箱密码登录
     *
     * @param userEmailLoginDTO 登录信息
     * @return 用户信息
     */
    @Override
    public User loginByPassword(UserEmailLoginDTO userEmailLoginDTO) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(userEmailLoginDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 密码校验
        String encryptedPassword = DigestUtils.md5DigestAsHex(userEmailLoginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus().equals(UserStatusConstant.BANNED)) {
            throw new RuntimeException("账号已被封禁");
        }

        return user;
    }

    /**
     * 邮箱验证码登录
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public User loginByEmail(String email) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus().equals(UserStatusConstant.BANNED)) {
            throw new RuntimeException("账号已被封禁");
        }

        return user;
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDTO 重置密码信息
     */
    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 根据邮箱查询用户
        User user = userMapper.getByEmail(resetPasswordDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新密码
        user.setPassword(DigestUtils.md5DigestAsHex(resetPasswordDTO.getNewPassword().getBytes()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 获取用户统计信息
     *
     * @param userId 用户ID
     * @return 用户统计信息，包括博客数、关注数、粉丝数、获赞数和总浏览数
     */
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. 获取用户博客数
            Integer blogCount = blogMapper.countByUserId(userId);

            // 2. 获取用户关注数
            Integer followCount = userFollowMapper.countUserFollows(userId);

            // 3. 获取用户粉丝数
            User user = userMapper.getById(userId);
            Long fansCount = user != null ? user.getFans() : 0L;

            // 4. 获取用户获赞数
            Integer likeCount = likeMapper.countUserReceived(userId);

            // 5. 获取用户博客浏览总数
            Integer viewCount = viewMapper.countUserBlogViews(userId);

            // 填充统计数据
            stats.put("blogCount", blogCount != null ? blogCount : 0);
            stats.put("followCount", followCount != null ? followCount : 0);
            stats.put("fansCount", fansCount != null ? fansCount : 0);
            stats.put("likeCount", likeCount != null ? likeCount : 0);
            stats.put("viewCount", viewCount != null ? viewCount : 0);

            return stats;
        } catch (Exception e) {
            log.error("获取用户统计信息出错: {}", e.getMessage(), e);
            stats.put("blogCount", 0);
            stats.put("followCount", 0);
            stats.put("fansCount", 0);
            stats.put("likeCount", 0);
            stats.put("viewCount", 0);
            return stats;
        }
    }

    /**
     * 清除用户相关缓存
     * 
     * @param userId 用户ID
     */
    private void evictUserCache(Long userId) {
        try {
            // 清除用户缓存
            blogCacheService.evictUserCache(userId);

            log.debug("用户缓存清除成功，用户ID：{}", userId);
        } catch (Exception e) {
            log.warn("清除用户缓存失败，用户ID：{}，错误信息：{}", userId, e.getMessage());
            // 缓存清除失败不应该影响业务流程
        }
    }
}

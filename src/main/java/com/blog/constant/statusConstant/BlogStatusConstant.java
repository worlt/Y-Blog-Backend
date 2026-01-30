package com.blog.constant.statusConstant;

/**
 * 博客状态常量类
 *
 * @Author worlt
 * @Date 2025/5/16 下午10:47
 */
public class BlogStatusConstant {

    // 待审核
    public static final Integer PENDING = 0;

    // 审核通过(正常状态)
    public static final Integer APPROVED = 1;

    // 拒绝
    public static final Integer REJECTED = 2;

    // 整改
    public static final Integer IN_REVIEW = 3;

    // 永久封禁
    public static final Integer PERMANENTLY_BANNED = 4;

    // 草稿
    public static final Integer DRAFT = 5;

    // 审核处理中（用于审核员标记正在处理）
    public static final Integer IN_PROCESS = 6;

}

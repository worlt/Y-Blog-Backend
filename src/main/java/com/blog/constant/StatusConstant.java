package com.blog.constant;

/**
 * 状态常量类 - 统一管理系统中的状态码，避免魔法数字
 * 
 * @author Worlt  
 * @date 2025/6/17
 */
public class StatusConstant {

    /**
     * 博客状态常量
     */
    public static class BlogStatus {
        /** 待审核 */
        public static final Integer PENDING_REVIEW = 0;
        /** 审核通过 */
        public static final Integer APPROVED = 1;
        /** 被拒绝 */
        public static final Integer REJECTED = 2;
        /** 整改中 */
        public static final Integer UNDER_REVISION = 3;
        /** 永久封禁 */
        public static final Integer PERMANENTLY_BANNED = 4;
        /** 草稿 */
        public static final Integer DRAFT = 5;
        
        public static String getStatusName(Integer status) {
            switch (status) {
                case 0: return "待审核";
                case 1: return "审核通过";
                case 2: return "被拒绝";
                case 3: return "整改中";
                case 4: return "永久封禁";
                case 5: return "草稿";
                default: return "未知状态";
            }
        }
    }

    /**
     * 用户状态常量
     */
    public static class UserStatus {
        /** 正常 */
        public static final Integer NORMAL = 0;
        /** 封禁 */
        public static final Integer BANNED = 1;
        
        public static String getStatusName(Integer status) {
            switch (status) {
                case 0: return "正常";
                case 1: return "封禁";
                default: return "未知状态";
            }
        }
    }

    /**
     * 用户角色常量
     */
    public static class UserRole {
        /** 管理员 */
        public static final Integer ADMIN = 0;
        /** 审核员 */
        public static final Integer AUDITOR = 1;
        /** 普通用户 */
        public static final Integer NORMAL_USER = 2;
        /** VIP用户 */
        public static final Integer VIP_USER = 3;
        
        public static String getRoleName(Integer role) {
            switch (role) {
                case 0: return "管理员";
                case 1: return "审核员";
                case 2: return "普通用户";
                case 3: return "VIP用户";
                default: return "未知角色";
            }
        }
    }

    /**
     * 举报状态常量
     */
    public static class ReportStatus {
        /** 待审核 */
        public static final Integer PENDING_REVIEW = 0;
        /** 处理中 */
        public static final Integer IN_PROGRESS = 1;
        /** 已处理 */
        public static final Integer PROCESSED = 2;
        
        public static String getStatusName(Integer status) {
            switch (status) {
                case 0: return "待审核";
                case 1: return "处理中";
                case 2: return "已处理";
                default: return "未知状态";
            }
        }
    }

    /**
     * 举报处理结果常量
     */
    public static class ReportResult {
        /** 正常 */
        public static final Integer NORMAL = 0;
        /** 整改 */
        public static final Integer REVISION = 1;
        /** 永久封禁 */
        public static final Integer PERMANENT_BAN = 2;
        /** 删除 */
        public static final Integer DELETE = 1;
        
        public static String getResultName(Integer result) {
            switch (result) {
                case 0: return "正常";
                case 1: return "整改/删除";
                case 2: return "永久封禁";
                default: return "未知结果";
            }
        }
    }

    /**
     * 评论状态常量
     */
    public static class CommentStatus {
        /** 正常 */
        public static final Integer NORMAL = 0;
        /** 被举报 */
        public static final Integer REPORTED = 1;
        /** 待审核 */
        public static final Integer PENDING_REVIEW = 2;
        /** 已删除 */
        public static final Integer DELETED = 3;
        
        public static String getStatusName(Integer status) {
            switch (status) {
                case 0: return "正常";
                case 1: return "被举报";
                case 2: return "待审核";
                case 3: return "已删除";
                default: return "未知状态";
            }
        }
    }

    /**
     * 审核结果常量
     */
    public static class ReviewResult {
        /** 通过 */
        public static final Integer APPROVED = 0;
        /** 拒绝 */
        public static final Integer REJECTED = 1;
        /** 整改 */
        public static final Integer REVISION = 2;
        /** 永久封禁 */
        public static final Integer PERMANENT_BAN = 3;
        
        public static String getResultName(Integer result) {
            switch (result) {
                case 0: return "通过";
                case 1: return "拒绝";
                case 2: return "整改";
                case 3: return "永久封禁";
                default: return "未知结果";
            }
        }
    }

    /**
     * 审核类型常量
     */
    public static class ReviewType {
        /** 用户 */
        public static final Integer USER = 0;
        /** 博客 */
        public static final Integer BLOG = 1;
        /** 评论 */
        public static final Integer COMMENT = 2;
        
        public static String getTypeName(Integer type) {
            switch (type) {
                case 0: return "用户";
                case 1: return "博客";
                case 2: return "评论";
                default: return "未知类型";
            }
        }
    }

    /**
     * 性别常量
     */
    public static class Gender {
        /** 男 */
        public static final Integer MALE = 0;
        /** 女 */
        public static final Integer FEMALE = 1;
        /** 未知 */
        public static final Integer UNKNOWN = 2;
        
        public static String getGenderName(Integer gender) {
            switch (gender) {
                case 0: return "男";
                case 1: return "女";
                case 2: return "未知";
                default: return "未知";
            }
        }
    }

    /**
     * 置顶状态常量
     */
    public static class TopStatus {
        /** 不置顶 */
        public static final Integer NOT_TOP = 0;
        /** 置顶 */
        public static final Integer TOP = 1;
        
        public static String getTopStatusName(Integer status) {
            switch (status) {
                case 0: return "不置顶";
                case 1: return "置顶";
                default: return "未知状态";
            }
        }
    }

    /**
     * 消息类型常量
     */
    public static class MessageType {
        /** 系统消息 */
        public static final String SYSTEM = "system";
        /** 用户消息 */
        public static final String USER = "user";
    }

    /**
     * 会话类型常量
     */
    public static class ConversationType {
        /** 系统通知 */
        public static final Integer SYSTEM_NOTIFICATION = 0;
        /** 用户私聊 */
        public static final Integer USER_CHAT = 1;
        
        public static String getTypeName(Integer type) {
            switch (type) {
                case 0: return "系统通知";
                case 1: return "用户私聊";
                default: return "未知类型";
            }
        }
    }

    /**
     * 布尔值常量
     */
    public static class BooleanValue {
        /** 否/假 */
        public static final Integer FALSE = 0;
        /** 是/真 */
        public static final Integer TRUE = 1;
    }
} 
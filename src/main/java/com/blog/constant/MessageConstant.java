package com.blog.constant;

/**
 * 信息提示常量类
 *
 * @Author worlt
 * @Date 2025/5/17 下午4:02
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被封禁";
    public static final String ALREADY_EXIT = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String CATEGORY_NOT_FOUND = "分类不存在";
    public static final String TITLE_IS_BLANK = "标题为空";
    public static final String CONTENT_IS_BLANK = "正文为空";
    public static final String BLOG_NOT_FOUND = "博客不存在";
    public static final String RELOGIN = "登录已过期，请重新登录";
    public static final String ADD_FAIL = "添加失败";
    public static final String DELETE_FAIL = "删除失败";
    public static final String MESSAGE_NOT_FOUND = "消息不存在";
    public static String DeleteException = "删除异常";
    public static String UPDATEException = "更新异常";

    // 点赞相关
    public static final String LIKE_FAIL = "点赞失败";
    public static final String UNLIKE_FAIL = "取消点赞失败";

    // 点踩相关
    public static final String DISLIKE_FAIL = "点踩失败";
    public static final String UNDISLIKE_FAIL = "取消点踩失败";
    
    // 收藏相关
    public static final String COLLECT_FAIL = "收藏失败";
    public static final String UNCOLLECT_FAIL = "取消收藏失败";
    
    // 关注相关
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String FOLLOW_SELF_FAIL = "不能关注自己";
    public static final String FOLLOW_FAIL = "关注失败";
    public static final String UNFOLLOW_FAIL = "取消关注失败";
    
    // 分享相关
    public static final String SHARE_FAIL = "分享失败";
    public static final String INVALID_PLATFORM = "无效的分享平台";
    public static final String PLATFORM_NOT_SUPPORTED = "不支持的分享平台";
    
    // 标签相关
    public static final String TAG_NOT_FOUND = "标签不存在";
    public static final String TAG_ALREADY_EXISTS = "标签已存在";
    public static final String TAG_ADD_FAIL = "标签添加失败";
    public static final String TAG_DELETE_FAIL = "标签删除失败";
    
    // 分类相关
    public static final String CATEGORY_ALREADY_EXISTS = "分类已存在";
    public static final String CATEGORY_ADD_FAIL = "分类添加失败";
    public static final String CATEGORY_DELETE_FAIL = "分类删除失败";
    public static final String CATEGORY_IN_USE = "分类正在使用中，无法删除";

    // 新增消息相关常量
    public static final String EMAIL_FORMAT_ERROR = "邮箱格式错误";
    public static final String EMAIL_ALREADY_EXISTS = "邮箱已存在";
    public static final String BLOG_ALREADY_EXISTS = "博客已存在";
    public static final String CONVERSATION_NOT_EXIST = "会话不存在";
    public static final String NO_PERMISSION = "您没有权限访问该会话";
    public static final String SEND_MESSAGE_FAILED = "发送消息失败";
}

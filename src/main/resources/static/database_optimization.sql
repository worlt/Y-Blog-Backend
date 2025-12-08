-- 数据库索引优化脚本（MySQL兼容版本）
-- 为关键查询字段添加索引，提升系统性能
-- @author Worlt
-- @date 2025/9/5

-- 注意：MySQL 5.7及以下版本不支持 IF NOT EXISTS，需要分别检查
-- 建议在执行前先检查索引是否已存在

-- 1. 博客表索引优化
-- 用户ID索引 - 查询用户的博客
CREATE INDEX idx_blog_user_id ON blog(user_id);

-- 分类ID索引 - 按分类查询博客
CREATE INDEX idx_blog_category_id ON blog(category_id);

-- 状态索引 - 查询特定状态的博客
CREATE INDEX idx_blog_status ON blog(status);

-- 发布时间索引 - 按时间排序
CREATE INDEX idx_blog_publish_time ON blog(publish_time);

-- 创建时间索引 - 按创建时间排序
CREATE INDEX idx_blog_create_time ON blog(create_time);

-- 置顶状态索引 - 查询置顶博客
CREATE INDEX idx_blog_is_top ON blog(is_top);

-- 复合索引：状态+发布时间 - 查询已发布的博客并按时间排序
CREATE INDEX idx_blog_status_publish_time ON blog(status, publish_time DESC);

-- 复合索引：用户ID+状态 - 查询用户特定状态的博客
CREATE INDEX idx_blog_user_status ON blog(user_id, status);

-- 2. 评论表索引优化
-- 博客ID索引 - 查询博客的评论
CREATE INDEX idx_comment_blog_id ON comment(blog_id);

-- 用户ID索引 - 查询用户的评论
CREATE INDEX idx_comment_user_id ON comment(user_id);

-- 父评论ID索引 - 查询回复评论
CREATE INDEX idx_comment_parent_id ON comment(parent_id);

-- 状态索引 - 查询特定状态的评论
CREATE INDEX idx_comment_status ON comment(status);

-- 创建时间索引 - 按时间排序评论
CREATE INDEX idx_comment_create_time ON comment(create_time);

-- 复合索引：博客ID+状态+创建时间 - 查询博客的有效评论
CREATE INDEX idx_comment_blog_status_time ON comment(blog_id, status, create_time DESC);

-- 3. 用户表索引优化
-- 用户名索引 - 登录查询
CREATE INDEX idx_user_username ON user(username);

-- 邮箱索引 - 邮箱登录查询
CREATE INDEX idx_user_email ON user(email);

-- 状态索引 - 查询特定状态的用户
CREATE INDEX idx_user_status ON user(status);

-- 4. 🚀 博客详情查询专用索引优化
-- 博客标签关联表索引 - 优化标签查询
CREATE INDEX idx_blog_tag_blog_id ON blog_tag(blog_id);
CREATE INDEX idx_blog_tag_tag_id ON blog_tag(tag_id);

-- 博客自定义标签关联表索引 - 优化自定义标签查询
CREATE INDEX idx_blog_custom_tag_blog_id ON blog_custom_tag(blog_id);

-- 浏览记录表索引 - 优化浏览记录查询
CREATE INDEX idx_view_user_blog ON view(user_id, blog_id);
CREATE INDEX idx_view_blog_id ON view(blog_id);
CREATE INDEX idx_view_user_id ON view(user_id);
CREATE INDEX idx_view_time ON view(view_time);

-- 分类表索引 - 优化分类查询
CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_status ON category(status);

-- 5. 点赞收藏相关索引优化
-- 点赞表索引
CREATE INDEX idx_like_user_blog ON `like`(user_id, blog_id);
CREATE INDEX idx_like_blog_id ON `like`(blog_id);

-- 收藏表索引  
CREATE INDEX idx_collection_user_blog ON collection(user_id, blog_id);
CREATE INDEX idx_collection_blog_id ON collection(blog_id);

-- 关注表索引
CREATE INDEX idx_follow_follower_followed ON follow(follower_id, followed_id);

-- 6. 复合索引优化查询性能
-- 博客列表查询复合索引
CREATE INDEX idx_blog_list_query ON blog(status, is_top, publish_time DESC);

-- 用户博客查询复合索引
CREATE INDEX idx_user_blog_query ON blog(user_id, status, publish_time DESC);

-- 分类博客查询复合索引
CREATE INDEX idx_category_blog_query ON blog(category_id, status, publish_time DESC);

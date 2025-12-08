-- 文章详情页性能优化索引脚本
-- 专门针对文章详情页加载慢的问题进行优化
-- @author Worlt
-- @date 2025/1/21

-- 检查并创建索引的安全方式（适用于MySQL 8.0+）
-- 如果使用MySQL 5.7，请手动检查索引是否存在后再执行

-- 1. 博客表核心索引（提升主查询性能）
-- 主键查询已有索引，无需额外创建

-- 用户ID索引 - 查询作者信息时使用
CREATE INDEX IF NOT EXISTS idx_blog_user_id ON blog(user_id);

-- 分类ID索引 - 查询分类信息时使用  
CREATE INDEX IF NOT EXISTS idx_blog_category_id ON blog(category_id);

-- 2. 用户表索引（提升作者信息查询）
-- 主键查询已有索引，用户名和邮箱通常也有唯一索引

-- 3. 分类表索引（提升分类信息查询）
-- 主键查询已有索引

-- 4. 博客标签关联表索引（提升标签查询性能）
CREATE INDEX IF NOT EXISTS idx_blog_tag_blog_id ON blog_tag(blog_id);
CREATE INDEX IF NOT EXISTS idx_blog_tag_tag_id ON blog_tag(tag_id);

-- 5. 博客自定义标签表索引（提升自定义标签查询性能）
CREATE INDEX IF NOT EXISTS idx_blog_custom_tag_blog_id ON blog_custom_tag(blog_id);

-- 6. 浏览记录表索引（提升浏览量记录性能）
CREATE INDEX IF NOT EXISTS idx_view_user_blog ON view(user_id, blog_id);
CREATE INDEX IF NOT EXISTS idx_view_blog_id ON view(blog_id);

-- 7. 点赞表索引（提升点赞状态查询性能）
CREATE INDEX IF NOT EXISTS idx_like_user_blog ON `like`(user_id, blog_id);

-- 8. 点踩表索引（提升点踩状态查询性能）  
CREATE INDEX IF NOT EXISTS idx_dislike_user_blog ON dislike(user_id, blog_id);

-- 9. 收藏表索引（提升收藏状态查询性能）
CREATE INDEX IF NOT EXISTS idx_collection_user_blog ON collection(user_id, blog_id);

-- 10. 关注表索引（提升关注状态查询性能）
CREATE INDEX IF NOT EXISTS idx_follow_follower_followed ON follow(follower_id, followed_id);

-- 11. 评论表索引（提升评论查询性能）
CREATE INDEX IF NOT EXISTS idx_comment_blog_id ON comment(blog_id);
CREATE INDEX IF NOT EXISTS idx_comment_status ON comment(status);
CREATE INDEX IF NOT EXISTS idx_comment_parent_id ON comment(parent_id);

-- 复合索引：博客ID+状态+创建时间（优化评论列表查询）
CREATE INDEX IF NOT EXISTS idx_comment_blog_status_time ON comment(blog_id, status, create_time DESC);

-- 12. 相关推荐查询优化索引
-- 博客列表查询复合索引（优化相关推荐查询）
CREATE INDEX IF NOT EXISTS idx_blog_status_publish_time ON blog(status, publish_time DESC);

-- 13. 用户统计信息查询优化索引
-- 用户博客数量统计
CREATE INDEX IF NOT EXISTS idx_blog_user_status ON blog(user_id, status);

-- 用户关注/粉丝统计
CREATE INDEX IF NOT EXISTS idx_follow_followed_id ON follow(followed_id);
CREATE INDEX IF NOT EXISTS idx_follow_follower_id ON follow(follower_id);

-- 执行完成提示
SELECT '文章详情页性能优化索引创建完成！' as message;

-- 查看创建的索引（可选）
-- SHOW INDEX FROM blog;
-- SHOW INDEX FROM view;
-- SHOW INDEX FROM blog_tag;
-- SHOW INDEX FROM blog_custom_tag; 
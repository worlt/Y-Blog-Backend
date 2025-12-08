-- 文章详情页性能优化索引脚本（最简版本）
-- 专门针对文章详情页加载慢的问题进行优化
-- 兼容所有MySQL版本，忽略"索引已存在"错误
-- @author Worlt
-- @date 2025/1/21

-- 注意：执行时可能会出现"Duplicate key name"错误，这是正常的，表示索引已存在

SELECT '🚀 开始创建文章详情页性能优化索引...' as message;

-- 1. 博客表核心索引（提升主查询性能）
CREATE INDEX idx_blog_user_id ON blog(user_id);
CREATE INDEX idx_blog_category_id ON blog(category_id);
CREATE INDEX idx_blog_status ON blog(status);
CREATE INDEX idx_blog_publish_time ON blog(publish_time);
CREATE INDEX idx_blog_is_top ON blog(is_top);

-- 复合索引
CREATE INDEX idx_blog_status_publish_time ON blog(status, publish_time DESC);
CREATE INDEX idx_blog_user_status ON blog(user_id, status);

-- 2. 用户表索引（提升作者信息查询）
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);  
CREATE INDEX idx_user_status ON user(status);

-- 3. 分类表索引（提升分类信息查询）
CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_status ON category(status);

-- 4. 博客标签关联表索引（提升标签查询性能）
CREATE INDEX idx_blog_tag_blog_id ON blog_tag(blog_id);
CREATE INDEX idx_blog_tag_tag_id ON blog_tag(tag_id);

-- 5. 博客自定义标签表索引（提升自定义标签查询性能）
CREATE INDEX idx_blog_custom_tag_blog_id ON blog_custom_tag(blog_id);

-- 6. 浏览记录表索引（提升浏览量记录性能）
CREATE INDEX idx_view_blog_id ON view(blog_id);
CREATE INDEX idx_view_user_id ON view(user_id);
CREATE INDEX idx_view_time ON view(view_time);
CREATE INDEX idx_view_user_blog ON view(user_id, blog_id);

-- 7. 点赞表索引（提升点赞状态查询性能）
CREATE INDEX idx_like_blog_id ON `like`(blog_id);
CREATE INDEX idx_like_user_blog ON `like`(user_id, blog_id);

-- 8. 收藏表索引（提升收藏状态查询性能）
CREATE INDEX idx_collection_blog_id ON collection(blog_id);
CREATE INDEX idx_collection_user_blog ON collection(user_id, blog_id);

-- 9. 关注表索引（提升关注状态查询性能）
CREATE INDEX idx_follow_follower_followed ON follow(follower_id, followed_id);
CREATE INDEX idx_follow_followed_id ON follow(followed_id);
CREATE INDEX idx_follow_follower_id ON follow(follower_id);

-- 10. 评论表索引（提升评论查询性能）
CREATE INDEX idx_comment_blog_id ON comment(blog_id);
CREATE INDEX idx_comment_user_id ON comment(user_id);
CREATE INDEX idx_comment_parent_id ON comment(parent_id);
CREATE INDEX idx_comment_status ON comment(status);
CREATE INDEX idx_comment_create_time ON comment(create_time);

-- 复合索引：博客ID+状态+创建时间（优化评论列表查询）
CREATE INDEX idx_comment_blog_status_time ON comment(blog_id, status, create_time DESC);

-- 11. 博客列表查询优化索引
CREATE INDEX idx_blog_list_query ON blog(status, is_top, publish_time DESC);
CREATE INDEX idx_user_blog_query ON blog(user_id, status, publish_time DESC);
CREATE INDEX idx_category_blog_query ON blog(category_id, status, publish_time DESC);

SELECT '🎉 文章详情页性能优化索引创建完成！' as message;
SELECT '📊 如果看到"Duplicate key name"错误，请忽略，表示索引已存在' as note;
SELECT '✅ 建议执行以下命令查看创建的索引：' as tip;
SELECT 'SHOW INDEX FROM blog;' as command1;
SELECT 'SHOW INDEX FROM view;' as command2;
SELECT 'SHOW INDEX FROM blog_tag;' as command3; 
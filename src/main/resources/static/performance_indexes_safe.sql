-- 文章详情页性能优化索引脚本（兼容版本）
-- 专门针对文章详情页加载慢的问题进行优化
-- 兼容MySQL 5.7+ 和 MySQL 8.0+
-- @author Worlt
-- @date 2025/1/21

-- 设置分隔符，避免存储过程中的分号冲突
DELIMITER //

-- 创建安全索引的存储过程
CREATE PROCEDURE IF NOT EXISTS CreateIndexSafely(
    IN index_name VARCHAR(64),
    IN table_name VARCHAR(64), 
    IN column_definition TEXT
)
BEGIN
    DECLARE index_count INT DEFAULT 0;
    
    -- 检查索引是否已存在
    SELECT COUNT(*)
    INTO index_count
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE()
      AND table_name = table_name
      AND index_name = index_name;
    
    -- 如果索引不存在，则创建
    IF index_count = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', index_name, ' ON ', table_name, '(', column_definition, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('✅ 索引 ', index_name, ' 创建成功') AS result;
    ELSE
        SELECT CONCAT('⚠️ 索引 ', index_name, ' 已存在，跳过创建') AS result;
    END IF;
END //

DELIMITER ;

-- 开始创建索引
SELECT '🚀 开始创建文章详情页性能优化索引...' as message;

-- 1. 博客表核心索引
CALL CreateIndexSafely('idx_blog_user_id', 'blog', 'user_id');
CALL CreateIndexSafely('idx_blog_category_id', 'blog', 'category_id');
CALL CreateIndexSafely('idx_blog_status', 'blog', 'status');
CALL CreateIndexSafely('idx_blog_publish_time', 'blog', 'publish_time');
CALL CreateIndexSafely('idx_blog_is_top', 'blog', 'is_top');

-- 复合索引
CALL CreateIndexSafely('idx_blog_status_publish_time', 'blog', 'status, publish_time DESC');
CALL CreateIndexSafely('idx_blog_user_status', 'blog', 'user_id, status');

-- 2. 用户表索引
CALL CreateIndexSafely('idx_user_username', 'user', 'username');
CALL CreateIndexSafely('idx_user_email', 'user', 'email');
CALL CreateIndexSafely('idx_user_status', 'user', 'status');

-- 3. 分类表索引
CALL CreateIndexSafely('idx_category_name', 'category', 'name');
CALL CreateIndexSafely('idx_category_status', 'category', 'status');

-- 4. 博客标签关联表索引
CALL CreateIndexSafely('idx_blog_tag_blog_id', 'blog_tag', 'blog_id');
CALL CreateIndexSafely('idx_blog_tag_tag_id', 'blog_tag', 'tag_id');

-- 5. 博客自定义标签表索引
CALL CreateIndexSafely('idx_blog_custom_tag_blog_id', 'blog_custom_tag', 'blog_id');

-- 6. 浏览记录表索引
CALL CreateIndexSafely('idx_view_blog_id', 'view', 'blog_id');
CALL CreateIndexSafely('idx_view_user_id', 'view', 'user_id');
CALL CreateIndexSafely('idx_view_time', 'view', 'view_time');
CALL CreateIndexSafely('idx_view_user_blog', 'view', 'user_id, blog_id');

-- 7. 点赞表索引
CALL CreateIndexSafely('idx_like_blog_id', '`like`', 'blog_id');
CALL CreateIndexSafely('idx_like_user_blog', '`like`', 'user_id, blog_id');

-- 8. 收藏表索引
CALL CreateIndexSafely('idx_collection_blog_id', 'collection', 'blog_id');
CALL CreateIndexSafely('idx_collection_user_blog', 'collection', 'user_id, blog_id');

-- 9. 关注表索引
CALL CreateIndexSafely('idx_follow_follower_followed', 'follow', 'follower_id, followed_id');
CALL CreateIndexSafely('idx_follow_followed_id', 'follow', 'followed_id');
CALL CreateIndexSafely('idx_follow_follower_id', 'follow', 'follower_id');

-- 10. 评论表索引
CALL CreateIndexSafely('idx_comment_blog_id', 'comment', 'blog_id');
CALL CreateIndexSafely('idx_comment_user_id', 'comment', 'user_id');
CALL CreateIndexSafely('idx_comment_parent_id', 'comment', 'parent_id');
CALL CreateIndexSafely('idx_comment_status', 'comment', 'status');
CALL CreateIndexSafely('idx_comment_create_time', 'comment', 'create_time');

-- 复合索引
CALL CreateIndexSafely('idx_comment_blog_status_time', 'comment', 'blog_id, status, create_time DESC');

-- 11. 博客列表查询优化索引
CALL CreateIndexSafely('idx_blog_list_query', 'blog', 'status, is_top, publish_time DESC');
CALL CreateIndexSafely('idx_user_blog_query', 'blog', 'user_id, status, publish_time DESC');
CALL CreateIndexSafely('idx_category_blog_query', 'blog', 'category_id, status, publish_time DESC');

-- 清理存储过程
DROP PROCEDURE IF EXISTS CreateIndexSafely;

-- 完成提示
SELECT '🎉 文章详情页性能优化索引创建完成！' as message;
SELECT '📊 建议执行以下命令查看创建的索引：' as tip;
SELECT 'SHOW INDEX FROM blog;' as command1;
SELECT 'SHOW INDEX FROM view;' as command2;
SELECT 'SHOW INDEX FROM blog_tag;' as command3; 
-- 数据库索引优化脚本（MySQL安全版本）
-- 为关键查询字段添加索引，提升系统性能
-- @author Worlt
-- @date 2025/1/17

-- 使用存储过程安全创建索引，避免重复创建错误
DELIMITER $$

-- 创建安全索引的存储过程
CREATE PROCEDURE IF NOT EXISTS CreateIndexSafely(
    IN table_name VARCHAR(64),
    IN index_name VARCHAR(64), 
    IN column_definition VARCHAR(255)
)
BEGIN
    DECLARE index_exists INT DEFAULT 0;
    
    -- 检查索引是否已存在
    SELECT COUNT(*) INTO index_exists 
    FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = table_name 
    AND index_name = index_name;
    
    -- 如果索引不存在则创建
    IF index_exists = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', index_name, ' ON ', table_name, '(', column_definition, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('索引 ', index_name, ' 创建成功') AS result;
    ELSE
        SELECT CONCAT('索引 ', index_name, ' 已存在，跳过创建') AS result;
    END IF;
END$$

DELIMITER ;

-- 1. 博客表索引优化
CALL CreateIndexSafely('blog', 'idx_blog_user_id', 'user_id');
CALL CreateIndexSafely('blog', 'idx_blog_category_id', 'category_id');
CALL CreateIndexSafely('blog', 'idx_blog_status', 'status');
CALL CreateIndexSafely('blog', 'idx_blog_publish_time', 'publish_time');
CALL CreateIndexSafely('blog', 'idx_blog_create_time', 'create_time');
CALL CreateIndexSafely('blog', 'idx_blog_is_top', 'is_top');
CALL CreateIndexSafely('blog', 'idx_blog_status_publish_time', 'status, publish_time DESC');
CALL CreateIndexSafely('blog', 'idx_blog_user_status', 'user_id, status');

-- 2. 评论表索引优化
CALL CreateIndexSafely('comment', 'idx_comment_blog_id', 'blog_id');
CALL CreateIndexSafely('comment', 'idx_comment_user_id', 'user_id');
CALL CreateIndexSafely('comment', 'idx_comment_parent_id', 'parent_id');
CALL CreateIndexSafely('comment', 'idx_comment_status', 'status');
CALL CreateIndexSafely('comment', 'idx_comment_create_time', 'create_time');
CALL CreateIndexSafely('comment', 'idx_comment_blog_status_time', 'blog_id, status, create_time DESC');

-- 3. 用户表索引优化
CALL CreateIndexSafely('user', 'idx_user_username', 'username');
CALL CreateIndexSafely('user', 'idx_user_email', 'email');
CALL CreateIndexSafely('user', 'idx_user_status', 'status');
CALL CreateIndexSafely('user', 'idx_user_role', 'role');
CALL CreateIndexSafely('user', 'idx_user_create_time', 'create_time');

-- 4. 点赞表索引优化
CALL CreateIndexSafely('`like`', 'idx_like_user_id', 'user_id');
CALL CreateIndexSafely('`like`', 'idx_like_blog_id', 'blog_id');
CALL CreateIndexSafely('`like`', 'idx_like_comment_id', 'comment_id');
CALL CreateIndexSafely('`like`', 'idx_like_user_blog', 'user_id, blog_id');
CALL CreateIndexSafely('`like`', 'idx_like_user_comment', 'user_id, comment_id');

-- 5. 点踩表索引优化
CALL CreateIndexSafely('dislike', 'idx_dislike_user_id', 'user_id');
CALL CreateIndexSafely('dislike', 'idx_dislike_blog_id', 'blog_id');
CALL CreateIndexSafely('dislike', 'idx_dislike_comment_id', 'comment_id');
CALL CreateIndexSafely('dislike', 'idx_dislike_user_blog', 'user_id, blog_id');
CALL CreateIndexSafely('dislike', 'idx_dislike_user_comment', 'user_id, comment_id');

-- 6. 收藏表索引优化
CALL CreateIndexSafely('collection', 'idx_collection_user_id', 'user_id');
CALL CreateIndexSafely('collection', 'idx_collection_blog_id', 'blog_id');
CALL CreateIndexSafely('collection', 'idx_collection_collect_time', 'collect_time');
CALL CreateIndexSafely('collection', 'idx_collection_user_blog', 'user_id, blog_id');
CALL CreateIndexSafely('collection', 'idx_collection_user_time', 'user_id, collect_time DESC');

-- 7. 博客标签关联表索引优化
CALL CreateIndexSafely('blog_tag', 'idx_blog_tag_blog_id', 'blog_id');
CALL CreateIndexSafely('blog_tag', 'idx_blog_tag_tag_id', 'tag_id');

-- 8. 博客自定义标签表索引优化
CALL CreateIndexSafely('blog_custom_tag', 'idx_blog_custom_tag_blog_id', 'blog_id');
CALL CreateIndexSafely('blog_custom_tag', 'idx_blog_custom_tag_custom_tag', 'custom_tag');

-- 9. 举报表索引优化
CALL CreateIndexSafely('blog_report', 'idx_blog_report_user_id', 'user_id');
CALL CreateIndexSafely('blog_report', 'idx_blog_report_blog_id', 'blog_id');
CALL CreateIndexSafely('blog_report', 'idx_blog_report_status', 'status');
CALL CreateIndexSafely('blog_report', 'idx_blog_report_reviewer_id', 'reviewer_id');
CALL CreateIndexSafely('blog_report', 'idx_blog_report_report_time', 'report_time');

-- 10. 审核记录表索引优化
CALL CreateIndexSafely('review_record', 'idx_review_record_reviewer_id', 'reviewer_id');
CALL CreateIndexSafely('review_record', 'idx_review_record_blog_id', 'blog_id');
CALL CreateIndexSafely('review_record', 'idx_review_record_user_id', 'user_id');
CALL CreateIndexSafely('review_record', 'idx_review_record_review_time', 'review_time');
CALL CreateIndexSafely('review_record', 'idx_review_record_review_type', 'review_type');

-- 11. 浏览记录表索引优化
CALL CreateIndexSafely('view', 'idx_view_user_id', 'user_id');
CALL CreateIndexSafely('view', 'idx_view_blog_id', 'blog_id');
CALL CreateIndexSafely('view', 'idx_view_view_time', 'view_time');
CALL CreateIndexSafely('view', 'idx_view_user_blog', 'user_id, blog_id');
CALL CreateIndexSafely('view', 'idx_view_user_time', 'user_id, view_time DESC');

-- 12. 消息表索引优化
CALL CreateIndexSafely('message', 'idx_message_conversation_id', 'conversation_id');
CALL CreateIndexSafely('message', 'idx_message_sender_id', 'sender_id');
CALL CreateIndexSafely('message', 'idx_message_receiver_id', 'receiver_id');
CALL CreateIndexSafely('message', 'idx_message_send_time', 'send_time');
CALL CreateIndexSafely('message', 'idx_message_is_read', 'is_read');
CALL CreateIndexSafely('message', 'idx_message_conversation_time', 'conversation_id, send_time DESC');

-- 13. 会话表索引优化
CALL CreateIndexSafely('conversation', 'idx_conversation_user1_id', 'user1_id');
CALL CreateIndexSafely('conversation', 'idx_conversation_user2_id', 'user2_id');
CALL CreateIndexSafely('conversation', 'idx_conversation_update_time', 'update_time DESC');
CALL CreateIndexSafely('conversation', 'idx_conversation_type', 'type');

-- 14. 邮箱验证表索引优化
CALL CreateIndexSafely('email_verification', 'idx_email_verification_email', 'email');
CALL CreateIndexSafely('email_verification', 'idx_email_verification_expiration_time', 'expiration_time');
CALL CreateIndexSafely('email_verification', 'idx_email_verification_is_used', 'is_used');
CALL CreateIndexSafely('email_verification', 'idx_email_verification_email_used_exp', 'email, is_used, expiration_time');

-- 15. 管理员表索引优化
CALL CreateIndexSafely('admin', 'idx_admin_username', 'username');
CALL CreateIndexSafely('admin', 'idx_admin_email', 'email');
CALL CreateIndexSafely('admin', 'idx_admin_status', 'status');
CALL CreateIndexSafely('admin', 'idx_admin_role', 'role');

-- 16. 审核员表索引优化
CALL CreateIndexSafely('auditor', 'idx_auditor_username', 'username');
CALL CreateIndexSafely('auditor', 'idx_auditor_email', 'email');
CALL CreateIndexSafely('auditor', 'idx_auditor_status', 'status');
CALL CreateIndexSafely('auditor', 'idx_auditor_role', 'role');

-- 清理存储过程
DROP PROCEDURE IF EXISTS CreateIndexSafely;

-- 索引创建完成提示
SELECT '🎉 数据库索引优化完成！' AS 完成状态,
       '所有索引已安全创建，系统性能将得到显著提升' AS 说明;

-- 查看当前数据库的所有索引
SELECT 
    TABLE_NAME AS 表名,
    INDEX_NAME AS 索引名,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS 索引字段
FROM information_schema.statistics 
WHERE table_schema = DATABASE()
    AND INDEX_NAME != 'PRIMARY'
GROUP BY TABLE_NAME, INDEX_NAME
ORDER BY TABLE_NAME, INDEX_NAME; 
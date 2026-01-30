-- 为blog表添加reviewer_id字段
-- 用于跟踪标记博客审核为处理中的审核员

ALTER TABLE `blog` 
ADD COLUMN `reviewer_id` BIGINT NULL COMMENT '审核员ID（标记为处理中时设置）' AFTER `status`;

-- 添加索引以优化查询性能
CREATE INDEX `idx_reviewer_id` ON `blog`(`reviewer_id`);

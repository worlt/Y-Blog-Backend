
-- 建议表
CREATE TABLE `suggestion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID (逻辑外键)',
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '建议标题',
  `content` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '建议内容',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:未实现 1:已实现',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_status_create_time` (`status`,`create_time`) COMMENT '状态与时间联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='建议表';

-- 建议投票表
CREATE TABLE `suggestion_vote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `suggestion_id` bigint(20) NOT NULL COMMENT '建议ID (逻辑外键)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID (逻辑外键)',
  `type` tinyint(4) NOT NULL COMMENT '投票类型 0:不想要 1:想要',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_suggestion` (`user_id`,`suggestion_id`) COMMENT '每个用户对每个建议只能投一票',
  KEY `idx_suggestion_type` (`suggestion_id`,`type`) COMMENT '建议ID与投票类型联合索引 (用于统计票数)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='建议投票表';

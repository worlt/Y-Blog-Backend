/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 14/06/2025 23:30:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员唯一标识符',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员头像路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '管理员状态（0-正常、1-封禁）',
  `role` tinyint(1) NULL DEFAULT NULL COMMENT '管理员角色（0-管理员、1-审核员）',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for appeal
-- ----------------------------
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '申诉用户ID',
  `appeal_time` datetime NULL DEFAULT NULL COMMENT '申诉时间',
  `appeal_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申诉原因',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '申诉状态（0-待审核、1-处理中、2-已处理）',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核员ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_result` tinyint(1) NULL DEFAULT NULL COMMENT '审核结果（0-接受、1-拒绝）',
  `review_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核原因',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for auditor
-- ----------------------------
DROP TABLE IF EXISTS `auditor`;
CREATE TABLE `auditor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员唯一标识符',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员头像路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '管理员状态（0-正常、1-封禁）',
  `role` tinyint(1) NULL DEFAULT NULL COMMENT '管理员角色（0-管理员、1-审核员）',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '博客唯一标识符',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '博客标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '博客内容（HTML格式）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '博客创建时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '博客发布时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '博客更新时间',
  `views` bigint NULL DEFAULT NULL COMMENT '博客浏览量',
  `likes` bigint NULL DEFAULT NULL COMMENT '博客点赞数',
  `dislikes` bigint NULL DEFAULT NULL COMMENT '博客点踩数',
  `collections` bigint NULL DEFAULT NULL COMMENT '博客收藏数',
  `shares` bigint NULL DEFAULT NULL COMMENT '博客分享数',
  `comments` bigint NULL DEFAULT NULL COMMENT '评论数量',
  `user_id` bigint NULL DEFAULT NULL COMMENT '作者ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '博客状态（0-待审核、1-审核通过、2-被拒绝、3-整改中、4-永久封禁、5-草稿）',
  `is_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否置顶（0-不置顶、1-置顶）',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_custom_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_custom_tag`;
CREATE TABLE `blog_custom_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联唯一标识符',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '博客ID',
  `custom_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义标签',
  `create_time` datetime NULL DEFAULT NULL COMMENT '关联创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_report
-- ----------------------------
DROP TABLE IF EXISTS `blog_report`;
CREATE TABLE `blog_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '举报用户ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '被举报博客ID',
  `report_time` datetime NULL DEFAULT NULL COMMENT '举报时间',
  `report_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '举报原因',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '举报状态（0-待审核、1-处理中、2-已处理）',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核员ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_result` tinyint(1) NULL DEFAULT NULL COMMENT '审核结果（0-正常、1-整改、2-永久封禁）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联唯一标识符',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '博客ID',
  `tag_id` bigint NULL DEFAULT NULL COMMENT '标签ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '关联创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类唯一标识符',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '分类创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '分类更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '收藏用户ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '收藏博客ID',
  `collect_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论唯一标识符',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '评论者ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '博客ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID（用于评论回复）',
  `likes` bigint NULL DEFAULT NULL COMMENT '评论点赞数',
  `dislikes` bigint NULL DEFAULT NULL COMMENT '评论点踩数',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '评论状态（0-正常、1-被举报、2-待审核、3-已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment_report
-- ----------------------------
DROP TABLE IF EXISTS `comment_report`;
CREATE TABLE `comment_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '举报用户ID',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '被举报评论ID',
  `report_time` datetime NULL DEFAULT NULL COMMENT '举报时间',
  `report_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '举报原因',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '举报状态（0-待审核、1-处理中、2-已处理）',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核员ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核结果（0-正常、1-删除）',
  `review_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话唯一标识符',
  `user1_id` bigint NOT NULL COMMENT '用户1ID（较小的用户ID）',
  `user2_id` bigint NOT NULL COMMENT '用户2ID（较大的用户ID）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `last_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后一条消息预览',
  `unread_count_1` int NULL DEFAULT 0 COMMENT '用户1未读消息数',
  `unread_count_2` int NULL DEFAULT 0 COMMENT '用户2未读消息数',
  `type` tinyint NULL DEFAULT 1 COMMENT '会话类型：0-系统通知，1-用户私聊',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dislike
-- ----------------------------
DROP TABLE IF EXISTS `dislike`;
CREATE TABLE `dislike`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点踩唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '点踩用户ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '点踩博客ID',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '点踩评论ID',
  `dislike_time` datetime NULL DEFAULT NULL COMMENT '点踩时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for email_verification
-- ----------------------------
DROP TABLE IF EXISTS `email_verification`;
CREATE TABLE `email_verification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '验证码记录唯一标识符',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `verification_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '验证码',
  `expiration_time` datetime NULL DEFAULT NULL COMMENT '验证码过期时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '验证码创建时间',
  `is_used` tinyint(1) NULL DEFAULT NULL COMMENT '验证码是否已使用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '点赞用户ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '点赞博客ID',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '点赞评论ID',
  `like_time` datetime NULL DEFAULT NULL COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一标识符',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '消息类型：0-系统消息，1-用户消息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for review_record
-- ----------------------------
DROP TABLE IF EXISTS `review_record`;
CREATE TABLE `review_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录唯一标识符',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核员ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '审核博客ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '审核用户ID',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '审核评论ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_result` tinyint(1) NULL DEFAULT NULL COMMENT '审核结果（0-通过、1-拒绝、2-整改、3-永久封禁）',
  `review_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核原因',
  `message_to_author` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核员给作者的消息',
  `review_type` tinyint(1) NULL DEFAULT NULL COMMENT '审核类型（0-用户、1-博客、2-评论）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分享唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '分享用户ID',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '分享博客ID',
  `share_time` datetime NULL DEFAULT NULL COMMENT '分享时间',
  `share_platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分享平台（如：微信、微博、QQ）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签唯一标识符',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '标签创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '标签更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识符',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小程序 openid',
  `unionid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小程序 unionid',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '用户状态（0-正常、1-封禁）',
  `role` tinyint(1) NULL DEFAULT NULL COMMENT '用户角色（0-管理员、1-审核员、2-普通用户、3-VIP用户）',
  `fans` bigint NULL DEFAULT NULL COMMENT '粉丝数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_follow
-- ----------------------------
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注关系唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关注者ID',
  `followed_user_id` bigint NULL DEFAULT NULL COMMENT '被关注者ID',
  `follow_time` datetime NULL DEFAULT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_report
-- ----------------------------
DROP TABLE IF EXISTS `user_report`;
CREATE TABLE `user_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报唯一标识符',
  `user_id` bigint NULL DEFAULT NULL COMMENT '举报用户ID',
  `reported_user_id` bigint NULL DEFAULT NULL COMMENT '被举报用户ID',
  `report_time` datetime NULL DEFAULT NULL COMMENT '举报时间',
  `report_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '举报原因',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '举报状态（0-待审核、1-处理中、2-已处理）',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核员ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_result` tinyint(1) NULL DEFAULT NULL COMMENT '审核结果（0-正常、1-封禁）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for view
-- ----------------------------
DROP TABLE IF EXISTS `view`;
CREATE TABLE `view`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `blog_id` bigint NOT NULL COMMENT '博客ID',
  `view_time` datetime NOT NULL COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浏览记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

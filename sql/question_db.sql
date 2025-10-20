/*
 Navicat Premium Dump SQL

 Source Server         : 小小怪
 Source Server Type    : MySQL
 Source Server Version : 90300 (9.3.0)
 Source Host           : localhost:3306
 Source Schema         : question_db

 Target Server Type    : MySQL
 Target Server Version : 90300 (9.3.0)
 File Encoding         : 65001

 Date: 20/10/2025 13:33:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam_questions
-- ----------------------------
DROP TABLE IF EXISTS `exam_questions`;
CREATE TABLE `exam_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NULL DEFAULT 1 COMMENT '题目顺序',
  `question_score` decimal(5, 2) NULL DEFAULT 5.00 COMMENT '题目分值',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `score` decimal(5, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_question`(`exam_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_question_order`(`question_order` ASC) USING BTREE,
  CONSTRAINT `exam_questions_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '考试题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_questions
-- ----------------------------
INSERT INTO `exam_questions` VALUES (1, 1, 23, 1, 20.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (2, 1, 24, 2, 30.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (3, 1, 25, 3, 10.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (4, 1, 26, 4, 20.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (5, 1, 27, 5, 20.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (6, 2, 23, 1, 25.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (7, 2, 25, 2, 15.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (8, 2, 26, 3, 30.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (9, 2, 27, 4, 30.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (10, 3, 23, 1, 20.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (11, 3, 24, 2, 40.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (12, 3, 25, 3, 15.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (13, 3, 26, 4, 25.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (14, 3, 27, 5, 20.00, '2025-09-08 10:26:10', 0.00);
INSERT INTO `exam_questions` VALUES (15, 4, 24, 2, 5.00, '2025-09-08 11:15:26', 10.00);
INSERT INTO `exam_questions` VALUES (16, 4, 25, 1, 5.00, '2025-09-08 11:15:26', 10.00);
INSERT INTO `exam_questions` VALUES (17, 4, 26, 7, 5.00, '2025-09-08 13:00:33', 10.00);
INSERT INTO `exam_questions` VALUES (18, 4, 28, 8, 5.00, '2025-09-08 13:00:33', 10.00);
INSERT INTO `exam_questions` VALUES (19, 4, 23, 3, 5.00, '2025-09-08 13:00:33', 10.00);
INSERT INTO `exam_questions` VALUES (20, 4, 27, 6, 5.00, '2025-09-08 13:00:33', 10.00);
INSERT INTO `exam_questions` VALUES (21, 5, 30, 2, 5.00, '2025-10-19 15:18:48', 10.00);
INSERT INTO `exam_questions` VALUES (22, 5, 31, 1, 5.00, '2025-10-19 15:18:48', 10.00);

-- ----------------------------
-- Table structure for exam_records
-- ----------------------------
DROP TABLE IF EXISTS `exam_records`;
CREATE TABLE `exam_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始答题时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `total_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '总得分',
  `status` int NULL DEFAULT NULL,
  `duration_minutes` int NULL DEFAULT 0 COMMENT '实际用时(分钟)',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '答题IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `correct_count` int NULL DEFAULT NULL,
  `end_time` datetime(6) NULL DEFAULT NULL,
  `score` decimal(5, 2) NULL DEFAULT NULL,
  `unanswered_count` int NULL DEFAULT NULL,
  `wrong_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_student`(`exam_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_student_number`(`student_number` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_submit_time`(`submit_time` ASC) USING BTREE,
  CONSTRAINT `exam_records_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_records_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_records
-- ----------------------------
INSERT INTO `exam_records` VALUES (10, 4, 1, '2021001', '张三', '2025-09-10 07:29:01', '2025-09-10 09:29:01', NULL, 2, 120, NULL, NULL, '2025-09-10 09:29:01', '2025-09-10 09:29:01', 1, '2025-09-10 09:29:00.652462', 10.00, 0, 5);
INSERT INTO `exam_records` VALUES (11, 5, 1, '2021001', '张三', NULL, '2025-10-19 15:21:08', NULL, 2, 120, NULL, NULL, '2025-10-19 15:17:25', '2025-10-19 15:21:08', 2, '2025-10-19 15:21:08.321855', 20.00, 0, 0);

-- ----------------------------
-- Table structure for exams
-- ----------------------------
DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '考试描述',
  `start_time` datetime NOT NULL COMMENT '考试开始时间',
  `end_time` datetime NOT NULL COMMENT '考试结束时间',
  `duration` int NOT NULL COMMENT '考试时长(分钟)',
  `total_score` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '总分',
  `pass_score` decimal(5, 2) NULL DEFAULT 60.00 COMMENT '及格分数',
  `question_count` int NULL DEFAULT 0 COMMENT '题目数量',
  `status` int NULL DEFAULT NULL,
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
  INDEX `idx_created_by`(`created_by` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exams
-- ----------------------------
INSERT INTO `exams` VALUES (1, 'Java基础知识测试', 'Java编程基础知识考试，包含选择题和填空题', '2025-09-08 06:34:00', '2025-09-09 03:00:00', 120, 100.00, 60.00, 5, 1, NULL, NULL, '2025-10-19 15:50:51');
INSERT INTO `exams` VALUES (2, '数据库原理考试', 'MySQL数据库基础知识和SQL语句考试', '2025-09-08 15:01:00', '2025-09-09 16:00:00', 90, 100.00, 70.00, 4, 1, NULL, NULL, '2025-10-19 15:50:51');
INSERT INTO `exams` VALUES (3, 'Web开发综合测试', 'HTML、CSS、JavaScript综合应用考试', '2025-09-08 06:35:00', '2025-09-09 04:00:00', 100, 120.00, 72.00, 5, 1, NULL, NULL, '2025-10-19 15:50:51');
INSERT INTO `exams` VALUES (4, '测试', '测试', '2025-09-08 12:53:00', '2025-09-10 18:56:00', 120, 100.00, 60.00, 6, 1, NULL, NULL, '2025-10-19 15:50:51');
INSERT INTO `exams` VALUES (5, '测试123', '测试', '2025-10-19 14:38:00', '2025-10-24 14:38:00', 120, 100.00, 60.00, 2, 1, NULL, '2025-10-19 14:38:38', '2025-10-19 15:50:51');

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `type` enum('single','multiple','judge','fill','essay') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型：single-单选题，multiple-多选题，judge-判断题，fill-填空题，essay-问答题',
  `difficulty` enum('easy','medium','hard') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '难度等级：easy-简单，medium-中等，hard-困难',
  `options` json NULL COMMENT '选项（JSON数组，适用于选择题和判断题）',
  `correct_answers` json NULL COMMENT '正确答案索引（JSON数组，适用于选择题）',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案（适用于填空题和问答题）',
  `tags` json NULL COMMENT '题目标签（JSON数组）',
  `status` tinyint NULL DEFAULT 1,
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (23, 'Java中String类的特点', '以下关于Java中String类的描述，哪个是正确的？', 'single', 'medium', '[\"String是可变的\", \"String是不可变的\", \"String可以被继承\", \"String不是线程安全的\"]', '[\"B\"]', NULL, '[\"Java基础\", \"String类\"]', 1, NULL, '2025-09-05 13:59:19', '2025-09-05 13:59:19');
INSERT INTO `questions` VALUES (24, 'Java集合框架的特点', '以下哪些是Java集合框架的特点？（多选）', 'multiple', 'hard', '[\"支持泛型\", \"线程安全\", \"动态扩容\", \"统一接口\"]', '[\"A\", \"C\", \"D\"]', NULL, '[\"Java集合\", \"框架特性\"]', 1, NULL, '2025-09-05 13:59:19', '2025-09-05 13:59:19');
INSERT INTO `questions` VALUES (25, 'Java是面向对象语言', 'Java是一种纯面向对象的编程语言。', 'judge', 'easy', NULL, NULL, '正确', '[\"Java基础\", \"面向对象\"]', 1, NULL, '2025-09-05 13:59:19', '2025-09-05 14:02:33');
INSERT INTO `questions` VALUES (26, 'Java访问修饰符', 'Java中有四种访问修饰符：______、______、______、______。', 'fill', 'medium', NULL, NULL, 'public,protected,default,private', '[\"Java基础\", \"访问修饰符\"]', 1, NULL, '2025-09-05 13:59:19', '2025-09-05 14:02:33');
INSERT INTO `questions` VALUES (27, '面向对象三大特性', '请简述面向对象编程的三大特性及其作用。', 'essay', 'hard', NULL, NULL, '封装、继承、多态。封装隐藏内部实现细节；继承实现代码复用；多态提供统一接口。', '[\"面向对象\", \"编程思想\"]', 1, NULL, '2025-09-05 13:59:19', '2025-09-05 14:02:33');
INSERT INTO `questions` VALUES (28, '测试题目1', '测试题目内容1', 'single', 'medium', '[\"答案1\", \"答案2\", \"答案3\", \"答案4\"]', '[\"B\"]', NULL, '[\"Java基础\", \"String类\"]', 1, NULL, '2025-09-06 15:04:51', '2025-09-06 15:04:51');
INSERT INTO `questions` VALUES (29, '1231', '123', 'fill', 'medium', NULL, NULL, '123', NULL, 1, 1, '2025-09-06 15:34:32', '2025-09-06 15:34:32');
INSERT INTO `questions` VALUES (30, '测试A', '测试A', 'single', 'easy', '[\"A\", \"B\"]', '[\"0\"]', NULL, NULL, 1, 1, '2025-10-19 15:18:19', '2025-10-19 15:18:19');
INSERT INTO `questions` VALUES (31, '测试B', '测试B', 'single', 'easy', '[\"A\", \"B\"]', '[\"1\"]', NULL, NULL, 1, 1, '2025-10-19 15:18:37', '2025-10-19 15:18:37');

-- ----------------------------
-- Table structure for student_answers
-- ----------------------------
DROP TABLE IF EXISTS `student_answers`;
CREATE TABLE `student_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `exam_record_id` bigint NOT NULL COMMENT '考试记录ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `answer_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学生答案内容',
  `selected_options` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '选择题答案(如: A,B,C)',
  `is_correct` bit(1) NULL DEFAULT NULL,
  `score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `answer_time` datetime NULL DEFAULT NULL COMMENT '答题时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_record_question`(`exam_record_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_record_id`(`exam_record_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_is_correct`(`is_correct` ASC) USING BTREE,
  CONSTRAINT `student_answers_ibfk_1` FOREIGN KEY (`exam_record_id`) REFERENCES `exam_records` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_answers_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_answers_ibfk_3` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_answers_ibfk_4` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answers
-- ----------------------------
INSERT INTO `student_answers` VALUES (91, 10, 4, 23, 1, NULL, NULL, b'0', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', '[\"B\"]', '0');
INSERT INTO `student_answers` VALUES (92, 10, 4, 24, 1, NULL, NULL, b'0', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', '[\"A\", \"C\", \"D\"]', '[0]');
INSERT INTO `student_answers` VALUES (93, 10, 4, 25, 1, NULL, NULL, b'1', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', '正确', 'true');
INSERT INTO `student_answers` VALUES (94, 10, 4, 26, 1, NULL, NULL, b'0', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', 'public,protected,default,private', '测试');
INSERT INTO `student_answers` VALUES (95, 10, 4, 27, 1, NULL, NULL, b'0', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', '封装、继承、多态。封装隐藏内部实现细节；继承实现代码复用；多态提供统一接口。', '测试');
INSERT INTO `student_answers` VALUES (96, 10, 4, 28, 1, NULL, NULL, b'0', 0.00, '2025-09-10 09:29:01', '2025-09-10 09:29:01', '2025-09-10 09:29:01', '[\"B\"]', '0');
INSERT INTO `student_answers` VALUES (99, 11, 5, 30, 1, NULL, NULL, b'1', 0.00, '2025-10-19 15:21:08', '2025-10-19 15:21:08', '2025-10-19 15:21:08', '[\"0\"]', '0');
INSERT INTO `student_answers` VALUES (100, 11, 5, 31, 1, NULL, NULL, b'1', 0.00, '2025-10-19 15:21:08', '2025-10-19 15:21:08', '2025-10-19 15:21:08', '[\"1\"]', '1');

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密后)',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级',
  `status` tinyint NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_number`(`student_number` ASC) USING BTREE,
  INDEX `idx_student_number`(`student_number` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_class_name`(`class_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES (1, '2021001', '张三', '$2a$10$xmsbYOInmss4t8sSnEUem.p0KEYTOMoWQ4fnVRy/Iv92eGViR/Ic2', 'zhangsan@example.com', '13800138001', '计算机1班', '计算机科学与技术', '2021', 1, '2025-09-08 10:05:32', '2025-10-19 15:19:45');
INSERT INTO `students` VALUES (2, '2021002', '李四', '$2a$10$xmsbYOInmss4t8sSnEUem.p0KEYTOMoWQ4fnVRy/Iv92eGViR/Ic2', 'lisi@example.com', '13800138002', '计算机1班', '计算机科学与技术', '2021', 1, '2025-09-08 10:05:32', '2025-09-08 10:16:03');
INSERT INTO `students` VALUES (3, '2021003', '王五', '$2a$10$xmsbYOInmss4t8sSnEUem.p0KEYTOMoWQ4fnVRy/Iv92eGViR/Ic2', 'wangwu@example.com', '13800138003', '计算机2班', '计算机科学与技术', '2021', 1, '2025-09-08 10:05:32', '2025-09-08 10:16:03');
INSERT INTO `students` VALUES (4, '2022001', '赵六', '$2a$10$xmsbYOInmss4t8sSnEUem.p0KEYTOMoWQ4fnVRy/Iv92eGViR/Ic2', 'zhaoliu@example.com', '13800138004', '软件1班', '软件工程', '2022', 1, '2025-09-08 10:05:32', '2025-09-08 10:16:03');
INSERT INTO `students` VALUES (5, '2022002', '钱七', '$2a$10$xmsbYOInmss4t8sSnEUem.p0KEYTOMoWQ4fnVRy/Iv92eGViR/Ic2', 'qianqi@example.com', '13800138005', '软件1班', '软件工程', '2022', 1, '2025-09-08 10:05:32', '2025-09-08 10:16:03');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密后）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1,
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123456', 'admin@example.com', NULL, '管理员', NULL, 1, '2025-10-19 15:20:40', '2025-09-05 10:16:48', '2025-10-19 15:20:40');
INSERT INTO `users` VALUES (2, 'user1', '123456', 'user1@example.com', NULL, '用户1', NULL, 1, NULL, '2025-09-05 10:16:48', '2025-09-05 10:16:48');
INSERT INTO `users` VALUES (3, 'test', '123456', 'test@example.com', NULL, '测试用户', NULL, 1, NULL, '2025-09-05 10:16:48', '2025-09-05 10:16:48');

-- ----------------------------
-- View structure for exam_statistics
-- ----------------------------
DROP VIEW IF EXISTS `exam_statistics`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `exam_statistics` AS select `e`.`id` AS `exam_id`,`e`.`title` AS `exam_title`,`e`.`start_time` AS `start_time`,`e`.`end_time` AS `end_time`,`e`.`duration` AS `duration`,`e`.`total_score` AS `total_score`,`e`.`pass_score` AS `pass_score`,count(distinct `eq`.`question_id`) AS `actual_question_count`,count(distinct `er`.`student_id`) AS `participant_count`,count((case when (`er`.`status` = 2) then 1 end)) AS `submitted_count`,count((case when ((`er`.`status` = 3) and (`er`.`total_score` >= `e`.`pass_score`)) then 1 end)) AS `passed_count`,round(avg((case when (`er`.`status` = 3) then `er`.`total_score` end)),2) AS `average_score`,max((case when (`er`.`status` = 3) then `er`.`total_score` end)) AS `highest_score`,min((case when (`er`.`status` = 3) then `er`.`total_score` end)) AS `lowest_score` from ((`exams` `e` left join `exam_questions` `eq` on((`e`.`id` = `eq`.`exam_id`))) left join `exam_records` `er` on((`e`.`id` = `er`.`exam_id`))) group by `e`.`id`,`e`.`title`,`e`.`start_time`,`e`.`end_time`,`e`.`duration`,`e`.`total_score`,`e`.`pass_score`;

-- ----------------------------
-- View structure for student_exam_statistics
-- ----------------------------
DROP VIEW IF EXISTS `student_exam_statistics`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `student_exam_statistics` AS select `s`.`id` AS `student_id`,`s`.`student_number` AS `student_number`,`s`.`name` AS `student_name`,`s`.`class_name` AS `class_name`,count(`er`.`id`) AS `total_exams`,count((case when (`er`.`status` = 3) then 1 end)) AS `completed_exams`,count((case when ((`er`.`status` = 3) and (`er`.`total_score` >= `e`.`pass_score`)) then 1 end)) AS `passed_exams`,round(avg((case when (`er`.`status` = 3) then `er`.`total_score` end)),2) AS `average_score`,max((case when (`er`.`status` = 3) then `er`.`total_score` end)) AS `highest_score`,min((case when (`er`.`status` = 3) then `er`.`total_score` end)) AS `lowest_score` from ((`students` `s` left join `exam_records` `er` on((`s`.`id` = `er`.`student_id`))) left join `exams` `e` on((`er`.`exam_id` = `e`.`id`))) group by `s`.`id`,`s`.`student_number`,`s`.`name`,`s`.`class_name`;

SET FOREIGN_KEY_CHECKS = 1;

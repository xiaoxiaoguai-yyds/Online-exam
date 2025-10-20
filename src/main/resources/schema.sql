-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS question_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE question_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建题目表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '题目标题',
    content TEXT NOT NULL COMMENT '题目内容',
    type ENUM('single', 'multiple', 'judge', 'fill', 'essay') NOT NULL COMMENT '题目类型：single-单选题，multiple-多选题，judge-判断题，fill-填空题，essay-问答题',
    difficulty ENUM('easy', 'medium', 'hard') NOT NULL COMMENT '难度等级：easy-简单，medium-中等，hard-困难',
    options JSON COMMENT '选项（JSON数组，适用于选择题和判断题）',
    correct_answers JSON COMMENT '正确答案索引（JSON数组，适用于选择题）',
    correct_answer TEXT COMMENT '正确答案（适用于填空题和问答题）',
    tags JSON COMMENT '题目标签（JSON数组）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_by BIGINT COMMENT '创建者ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- 插入测试数据（使用明文密码）
INSERT INTO users (username, password, email, nickname, status) VALUES 
('admin', '123456', 'admin@example.com', '管理员', 1),
('user1', '123456', 'user1@example.com', '用户1', 1),
('test', '123456', 'test@example.com', '测试用户', 1)
ON DUPLICATE KEY UPDATE password=VALUES(password);

-- 插入题目测试数据
INSERT INTO questions (title, content, type, difficulty, options, correct_answers, tags, created_by) VALUES 
('JavaScript基础语法', '以下哪个是JavaScript中声明变量的正确方式？', 'single', 'easy', 
 '["var x = 10;", "variable x = 10;", "v x = 10;", "declare x = 10;"]', 
 '[0]', 
 '["JavaScript", "基础", "语法"]', 1),

('Vue.js组件通信', 'Vue.js中父组件向子组件传递数据使用什么？', 'single', 'medium', 
 '["props", "emit", "slot", "ref"]', 
 '[0]', 
 '["Vue.js", "组件", "通信"]', 1),

('HTTP状态码', '以下哪些是客户端错误的HTTP状态码？', 'multiple', 'medium', 
 '["400", "401", "404", "500"]', 
 '[0, 1, 2]', 
 '["HTTP", "状态码", "网络"]', 1),

('CSS布局判断', 'CSS中display:flex属性用于创建弹性布局。', 'judge', 'easy', 
 '["正确", "错误"]', 
 '[0]', 
 '["CSS", "布局", "Flexbox"]', 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 插入问答题数据（使用correct_answer字段）
INSERT INTO questions (title, content, type, difficulty, correct_answer, tags, created_by) VALUES 
('算法复杂度', '请简述快速排序算法的时间复杂度。', 'essay', 'hard', 
 '快速排序的平均时间复杂度为O(n log n)，最坏情况下为O(n²)，最好情况下为O(n log n)。空间复杂度为O(log n)。', 
 '["算法", "排序", "复杂度"]', 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);
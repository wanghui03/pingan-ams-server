-- 更新管理员密码为 admin123
-- 执行此脚本修复登录问题
UPDATE ams_user 
SET password = '$2a$10$PiSJH9sbYDGh5nQH/.Gua.HdMWhaaR1BMc52NLrt.DseEV0xx.eyu'
WHERE username = 'admin';

-- 验证更新结果
SELECT id, username, nickname, user_type, status 
FROM ams_user 
WHERE username = 'admin';

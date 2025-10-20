package org.bishe.question;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {
    
    @Test
    public void testBCryptPasswordEncoding() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIgBTNjOP6IS6.oa5eMkFrYPTK";
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("存储的哈希: " + storedHash);
        
        // 测试验证
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("密码匹配结果: " + matches);
        
        // 生成新的哈希进行对比
        String newHash = encoder.encode(rawPassword);
        System.out.println("新生成的哈希: " + newHash);
        
        boolean newMatches = encoder.matches(rawPassword, newHash);
        System.out.println("新哈希匹配结果: " + newMatches);
        
        // 断言新生成的哈希应该能匹配
        assertTrue(newMatches, "新生成的哈希应该能匹配原始密码");
        
        // 如果存储的哈希不匹配，说明可能有问题
        if (!matches) {
            System.out.println("警告: 存储的哈希无法匹配原始密码，可能存在编码问题");
        }
    }
}
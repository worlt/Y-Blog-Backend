package com.blog;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

/**
 * @Author worlt
 * @Date 2025/5/19 下午10:28
 */
public class Md5Test {

    @Test
    public void md5Test() {
        String password = "123456";
        System.out.println(DigestUtils.md5DigestAsHex(password.getBytes()));
    }
}

package com.clouddrive.modules.file;

import com.clouddrive.common.redis.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() throws JsonProcessingException {
        String s = DigestUtils.md5Hex("1231412313");
        System.out.print(s);
        System.out.print(s.length());
    }

    @Data
    class Range {
        long start;
        long end;
    }
}

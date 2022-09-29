package com.clouddrive.file;

import com.clouddrive.file.service.impl.UploadServiceImpl;
import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class FileApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() throws JsonProcessingException {

        String uploadData = "{\"hash\":\"12345\",\"size\":\"5000\",\"wrote\":[\"300-500\"]}";
        Map<String, Object> map = objectMapper.readValue(uploadData, new TypeReference<Map>() {
        });

        List<String> strList = (List<String>) map.get("wrote");

        List<Range> rangeList = new ArrayList<>();
        for (String s : strList) {
            String[] sl = s.split("-");
            Range range = new Range();
            range.setStart(Long.valueOf(sl[0]));
            range.setEnd(Long.valueOf(sl[1]));
            rangeList.add(range);
        }

        rangeList.sort((x, y) -> (int) (x.getStart() - y.getStart()));

        EditWaitList(100, 200, rangeList);
        EditWaitList(0, 100, rangeList);
        EditWaitList(500, 1000, rangeList);
        System.out.println();
    }

    //笨方法融合，但很好理解很有效果
    List<Range> EditWaitList(long start, long end, List<Range> waitList) {
        Range range = new Range();
        range.setStart(start);
        range.setEnd(end);
        waitList.add(range);
        waitList.sort((x, y) -> (int) (x.getStart() - y.getStart()));
        for (int i = 0; i < waitList.size() - 1; i++) {
            if (waitList.get(i).getEnd() >= waitList.get(i + 1).getStart()) {
                Range l = waitList.get(i);
                Range r = waitList.get(i + 1);
                l.setStart(l.start);
                l.setEnd(r.end);
                waitList.remove(i + 1);
                i--;
            }
        }
        return waitList;
    }

    @Data
    class Range {
        long start;
        long end;
    }
}

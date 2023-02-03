package com.clouddrive.main.rabbitmq;

import com.clouddrive.common.rabbitmq.constant.ExchangeConstant;
import com.clouddrive.common.redis.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class FindFileReturnMessageConsumer {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ExchangeConstant.ReturnFindFileDataQueueName),
            exchange = @Exchange(value = ExchangeConstant.ReturnFindFileDataExchangeName)
    ))
    public void getData(Map<String, String> map) throws JsonProcessingException {
        redisUtil.addStringAndSetTimeOut("downloadReturn:" + map.get("hashId"), objectMapper.writeValueAsString(map), 5);
    }
}

package com.clouddrive.modules.file.rocketmq;

import com.clouddrive.common.id.constant.WorkIDConstants;
import com.clouddrive.common.id.feign.GetIDFeign;
import com.clouddrive.common.metadata.constant.NodeIDConstants;
import com.clouddrive.common.rabbitmq.constant.ExchangeConstant;
import com.clouddrive.common.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
class FindFileMessageConsumer {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GetIDFeign getIDFeign;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${clouddrive.save-path}")
    String fileSavePath;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(value = ExchangeConstant.FindFileExchangeName, type = ExchangeTypes.FANOUT)
    ))
    public void getData(Map<String, String> map) {
        String[] hash = map.get("hash").split("_");
        String hashFolder = hash[0];
        String fileId = hash[1];
        File file = new File(Paths.get(fileSavePath, hashFolder, fileId).toString());
        if (!file.exists()) {
            return;
        }
        Long downloadId = getIDFeign.getID(WorkIDConstants.DownloadID);
        String mess = String.format("{\"hash\":\"%s\",\"fileId\":\"%s\",\"size\":\"%s\"}", hashFolder, fileId, file.length());

        redisUtil.addStringAndSetTimeOut("downloadTask:" + downloadId, mess, 5);

        Map<String, String> re = new HashMap<>();
        re.put("hashId", map.get("hash").toString());
        re.put("downloadId", downloadId.toString());
        re.put("nodeId", NodeIDConstants.NodeID.toString());
        rabbitTemplate.convertAndSend(ExchangeConstant.ReturnFindFileDataExchangeName,"",re);
        log.info("已记录Download");
        log.info(downloadId + "已发送");
    }
}

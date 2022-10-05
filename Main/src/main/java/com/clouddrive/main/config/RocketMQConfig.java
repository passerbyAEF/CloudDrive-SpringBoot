package com.clouddrive.main.config;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    @Bean(name = "updateTemplate")
    public RocketMQTemplate updateTemplate(RocketMQMessageConverter rocketMQMessageConverter, RocketMQProperties rocketMQProperties) throws MQClientException {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        RocketMQProperties.Consumer consumerConfig = rocketMQProperties.getConsumer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = consumerConfig.getGroup();
        String topicName = "file";
        String selectorExpression = "uploadReturn";
        String accessChannel = rocketMQProperties.getAccessChannel();

        MessageModel messageModel = MessageModel.valueOf(consumerConfig.getMessageModel());
        SelectorType selectorType = SelectorType.valueOf(consumerConfig.getSelectorType());
        int pullBatchSize = consumerConfig.getPullBatchSize();
        boolean useTLS = consumerConfig.isTlsEnable();
        DefaultLitePullConsumer litePullConsumer = RocketMQUtil.createDefaultLitePullConsumer(nameServer, accessChannel, groupName, topicName, messageModel, selectorType, selectorExpression, null, null, pullBatchSize, useTLS);
        litePullConsumer.setEnableMsgTrace(consumerConfig.isEnableMsgTrace());
        litePullConsumer.setCustomizedTraceTopic(consumerConfig.getCustomizedTraceTopic());
        litePullConsumer.setNamespace(consumerConfig.getNamespace());
        litePullConsumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());

        rocketMQTemplate.setConsumer(litePullConsumer);
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        return rocketMQTemplate;
    }
}

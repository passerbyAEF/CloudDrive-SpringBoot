package com.clouddrive.modules.file.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileFindExchangeConfig {
//    //交换机名称
//    static final String topicExchangeName = "Find.File.Exchange";

//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(topicExchangeName, true, false);
//    }
//
//    @Bean
//    public Queue fileQueue() {
//        return new Queue("file.fanout.find-file", true);
//    }
//
//    @Bean
//    public Binding findFileBinding() {
//        return BindingBuilder.bind(fileQueue()).to(fanoutExchange());
//    }
}

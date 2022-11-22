package com.clouddrive.modules.file.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.clouddrive.common.id.feign.GetIDFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class})
public class NacosDiscoveryClientConfiguration {

    @Autowired
    GetIDFeign getIDFeign;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties, WebApplicationContext webApplicationContext) throws Exception {
        //更改服务详情中的元数据，增加服务注册时间
//        nacosDiscoveryProperties.getMetadata().put("startup.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        Long id = getIDFeign.getID();
        if (id == -1) throw new Exception("服务访问失败！");
        return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
    }
}

package com.clouddrive.common.metadata.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.clouddrive.common.id.feign.GetIDFeign;
import com.clouddrive.common.metadata.constant.NodeIDConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;

@AutoConfiguration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({NacosDiscoveryClientConfiguration.class})
public class WordIdAutoSetConfiguration {

    @Autowired
    GetIDFeign getIDFeign;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties, WebApplicationContext webApplicationContext) throws Exception {
        Long id = getIDFeign.getNodeID();
        if (id == -1) throw new Exception("服务访问失败！");
        NodeIDConstants.NodeID = id;
        nacosDiscoveryProperties.getMetadata().put("workId", id.toString());
        return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
    }
}

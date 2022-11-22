package com.clouddrive.gateway.balancer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class FileServiceUrlBalancer {
    private final String serviceId;
    private final String workId;
    private ObjectProvider<ServiceInstanceListSupplier> supplierProvider;

    public Mono<Response<ServiceInstance>> choose() {
        ServiceInstanceListSupplier supplier = supplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get().next().map(this::getInstanceResponse);
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            log.warn("不存在这个服务: " + this.serviceId);
            return new EmptyResponse();
        }
        List<ServiceInstance> collect = serviceInstances.stream().filter((item) -> {
            Map<String, String> metadata = item.getMetadata();
            if (!metadata.containsKey("workId")) {
                return false;
            }
            return metadata.get("workId").equals(workId);
        }).collect(Collectors.toList());
        if (collect.size() == 0) {
            log.warn("找不到匹配的WorkId: " + this.serviceId);
            return new EmptyResponse();
        }
        return new DefaultResponse(collect.get(0));
    }
}

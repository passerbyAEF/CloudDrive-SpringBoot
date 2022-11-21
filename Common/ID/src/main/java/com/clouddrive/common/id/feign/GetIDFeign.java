package com.clouddrive.common.id.feign;

import com.clouddrive.common.id.feign.fallback.GetIDFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "IDServer", fallback = GetIDFeignFallback.class)
public interface GetIDFeign {

    @GetMapping("getID")
    Long getID();
}

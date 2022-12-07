package com.clouddrive.common.id.feign;

import com.clouddrive.common.id.feign.fallback.GetIDFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "IDServer", fallback = GetIDFeignFallback.class)
public interface GetIDFeign {

    @GetMapping("getID")
    Long getID(@RequestParam("workId") Integer workId);

    @GetMapping("getNodeID")
    Long getNodeID();
}

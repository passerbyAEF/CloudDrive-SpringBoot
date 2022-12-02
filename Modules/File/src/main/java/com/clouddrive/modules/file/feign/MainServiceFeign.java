package com.clouddrive.modules.file.feign;

import com.clouddrive.modules.file.feign.fallback.MainServiceFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "appMain", fallback = MainServiceFeignFallback.class)
public interface MainServiceFeign {
    @GetMapping("/System/UploadOK")
    Boolean UploadOK(@RequestParam("uploadId") String uploadId, @RequestParam("fileId") String fileId);
}

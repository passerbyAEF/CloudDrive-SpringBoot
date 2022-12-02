package com.clouddrive.main.feign;

import com.clouddrive.main.feign.fallback.FileCoreFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@FeignClient(name = "fileServer", fallback = FileCoreFeignFallback.class)
public interface FileCoreFeign {

    @GetMapping("/System/CreateUpdate")
    Map<String, String> CreateUpdate(@RequestParam("hash") String hash,@RequestParam("size") Long size);
}

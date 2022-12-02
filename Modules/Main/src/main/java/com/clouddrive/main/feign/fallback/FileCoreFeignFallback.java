package com.clouddrive.main.feign.fallback;

import com.clouddrive.main.feign.FileCoreFeign;

import java.util.Map;

public class FileCoreFeignFallback implements FileCoreFeign {
    @Override
    public Map<String, String> CreateUpdate(String hash, Long size) {
        return null;
    }
}

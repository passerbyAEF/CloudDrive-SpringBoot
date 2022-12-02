package com.clouddrive.modules.file.feign.fallback;

import com.clouddrive.modules.file.feign.MainServiceFeign;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MainServiceFeignFallback implements MainServiceFeign {
    @Override
    public Boolean UploadOK(String uploadId, String fileId) {
        return false;
    }
}

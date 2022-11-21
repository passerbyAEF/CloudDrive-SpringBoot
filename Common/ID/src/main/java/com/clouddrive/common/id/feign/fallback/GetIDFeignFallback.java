package com.clouddrive.common.id.feign.fallback;

import com.clouddrive.common.id.feign.GetIDFeign;

public class GetIDFeignFallback implements GetIDFeign {
    @Override
    public Long getID() {
        return -1L;
    }
}

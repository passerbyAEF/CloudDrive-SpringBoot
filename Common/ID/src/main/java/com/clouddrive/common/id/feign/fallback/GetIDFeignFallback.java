package com.clouddrive.common.id.feign.fallback;

import com.clouddrive.common.id.feign.GetIDFeign;

public class GetIDFeignFallback implements GetIDFeign {
    @Override
    public Long getID(Integer workId) {
        return -1L;
    }

    @Override
    public Long getNodeID() {
        return -1L;
    }
}

package com.clouddrive.common.id.util;

import com.clouddrive.common.id.service.SnowFlakeService;

public class SnowFlakeUtil {

    private static final SnowFlakeService snowFlakeService;

    static {
        snowFlakeService = new SnowFlakeService(0);
    }

    public static long getNext(int workId) {
        return snowFlakeService.nextId(workId);
    }
}

package com.clouddrive.modules.idserver.controller;

import com.clouddrive.common.core.properties.ServiceProperties;
import com.clouddrive.common.id.util.SnowFlakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
public class GetIDController {

    @Autowired
    ServiceProperties serviceProperties;

    @GetMapping("getID")
    public Long getID(Integer workId) {
        return SnowFlakeUtil.getNext(workId);
    }

    @GetMapping("getNodeID")
    public Long getID() {
        return SnowFlakeUtil.getNext(0);
    }
}

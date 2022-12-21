package com.clouddrive.modules.idserver.controller;

import com.clouddrive.common.id.util.SnowFlakeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
public class GetIDController {

    @GetMapping("getID")
    public Long getID(@RequestParam("workId") Integer workId) {
        return SnowFlakeUtil.getNext(workId);
    }

    @GetMapping("getNodeID")
    public Long getID() {
        return SnowFlakeUtil.getNext(0);
    }
}

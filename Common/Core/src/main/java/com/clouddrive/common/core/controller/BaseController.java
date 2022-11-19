package com.clouddrive.common.core.controller;

import com.clouddrive.common.core.constant.HttpStatus;
import com.clouddrive.common.core.domain.ReturnMode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseController {

    public ReturnMode<Object> Error() {
        return new ReturnMode<>(null, "Error");
    }

    public ReturnMode<Object> Error(String message) {
        return new ReturnMode<>(message, "Error");
    }

    public ReturnMode<Object> OK(Object data) {
        return new ReturnMode<>(data, "OK");
    }

    public ReturnMode<Object> OK() {
        return new ReturnMode<>(null, "OK");
    }

    public void GoToUrl(HttpServletResponse response, String url) throws IOException {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.sendRedirect("/");
    }
}

package com.clouddrive.common.core.controller;

import com.clouddrive.common.core.constant.HttpStatus;
import com.clouddrive.common.core.domain.ReturnMode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseController {

    public ReturnMode<Object> Error() {
        return Error(null, "错误！");
    }

    public ReturnMode<Object> Error(String message) {
        return Error(null, message);
    }

    public ReturnMode<Object> Error(Object data, String message) {
        return setDataAndReturn(data, message, HttpStatus.ERROR);
    }

    public ReturnMode<Object> OK() {
        return OK(null);
    }

    public ReturnMode<Object> OK(Object data) {
        return setDataAndReturn(data, "OK", HttpStatus.SUCCESS);
    }

    public ReturnMode<Object> setDataAndReturn(Object Data, String message, Integer status) {
        return new ReturnMode<>(Data, message, status);
    }

    public void GoToUrl(HttpServletResponse response, String url) throws IOException {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.sendRedirect(url);
    }

    public ReturnMode<Object> ReturnGoToUrl(String url) {
        return ReturnGoToUrl(url, "跳转");
    }

    public ReturnMode<Object> ReturnGoToUrl(String url, String message) {
        return setDataAndReturn(url, message, HttpStatus.SEE_OTHER);
    }
}

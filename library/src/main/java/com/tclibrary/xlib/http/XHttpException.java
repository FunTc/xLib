package com.tclibrary.xlib.http;

/**
 * Created by FunTc on 2018/10/26.
 * http系统的异常
 */
public class XHttpException extends RuntimeException {

    private int code;

    public XHttpException(int code, String message) {
        super(message);
        this.code = code;
        
    }

    public int code() {
        return code;
    }

}

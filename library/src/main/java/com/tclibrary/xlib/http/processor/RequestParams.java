package com.tclibrary.xlib.http.processor;

/**
 * Created by FunTc on 2018/11/2.
 */
public class RequestParams {

    private Object[] params;

    public RequestParams(Object... params) {
        this.params = params;
    }

    public void setParams(Object... params) {
        this.params = params;
    }

    public void setParam(int index, Object param) {
        if (params == null || index >= params.length) return;
        params[index] = param;
    }

    public Object[] getParams() {
        return params;
    }

    public Object getParam(int index) {
        if (params == null || index >= params.length) return null;
        return params[index];
    }

    public static RequestParams create(Object... params){
        return new RequestParams(params);
    }


}

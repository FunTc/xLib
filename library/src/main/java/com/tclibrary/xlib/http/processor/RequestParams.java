package com.tclibrary.xlib.http.processor;

/**
 * Created by TianCheng on 2018/11/2.
 */
public class RequestParams {

	Object[] params;
	
	public RequestParams(Object... params) {
		this.params = params;
	}

	
	public static RequestParams create(Object... params){
		return new RequestParams(params);
	}
	
	
}

package com.tclibrary.xlib.http;

/**
 * Created by FunTc on 2018/10/26.
 * 接口返回数据异常
 */
public class ResponseResultException extends XHttpException {
	
	public ResponseResultException(int code, String message){
		super(code, message);
	}
	
}

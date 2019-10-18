package com.tclibrary.xlib.http;

/**
 * Created by TianCheng on 2018/10/26.
 */
public class ResponseResultException extends Exception {
	
	private int code;
	private String message;
	
	public ResponseResultException(int code, String message){
		this.code = code;
		this.message = message;
	}
	
	public int getErrorCode(){
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

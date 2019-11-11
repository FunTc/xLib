package com.tclibrary.xlib.http;

/**
 * Created by FunTc on 2018/10/26.
 * 
 */
public interface IResponseResult {
	
	boolean isRequestSuccess();
	
	int getResponseCode();
	
	String getResponseMessage();
	
}

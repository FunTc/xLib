package com.tclibrary.xlib.http.log;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FunTc on 2018/10/30.
 */
public interface IHttpLogPrinter {

	/**
	 * 请求体中除过Body和Header的基本信息
	 */
	void printRequestBasic(Request request);

	/**
	 * 请求体中除过Header的其他信息
	 */
	void printRequestBasicAndBody(Request request, String formatBodyStr);

	/**
	 * 请求体中除过Body的其他信息
	 */
	void printRequestBasicAndHeader(Request request);

	/**
	 * 请求体中所有信息
	 */
	void printRequest(Request request, String formatBodyStr);

	
	
	
	/**
	 * 返回体中除过Body和Header的基本信息
	 */
	void printResponseBasic(long elapsedTime, Response response);

	/**
	 * 返回体中除过Header的其他信息
	 */
	void printResponseBasicAndBody(long elapsedTime, Response response, String formatBodyStr);

	/**
	 * 返回体中除过Body的其他信息
	 */
	void printResponseBasicAndHeader(long elapsedTime, Response response);

	/**
	 * 返回体中所有信息
	 */
	void printResponse(long elapsedTime, Response response, String formatBodyStr);

}

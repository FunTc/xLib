package com.tclibrary.xlib.http.log;

import android.text.TextUtils;
import android.util.Log;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FunTc on 2018/10/31.
 */
public class DefaultHttpLogPrinter implements IHttpLogPrinter {

	private static final String TAG = "HttpLog";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

	private static final String N = "\n";
	private static final String T = "\t";
	private static final String REQUEST_UP_LINE = "┌────── Request ────────────────────────────────────────────────────────────────────────";
	private static final String END_LINE = "└───────────────────────────────────────────────────────────────────────────────────────";
	private static final String RESPONSE_UP_LINE = "┌────── Response ───────────────────────────────────────────────────────────────────────";
	private static final String BODY_TAG = "Body: ";
	private static final String URL_TAG = "URL: ";
	private static final String METHOD_TAG = "Method: ";
	private static final String HEADERS_TAG = "Headers: ";
	private static final String STATUS_TAG = "Status: ";
	private static final String ELAPSED_TAG = "Elapsed Time: ";
	private static final String CORNER_UP = "┌ ";
	private static final String CORNER_BOTTOM = "└ ";
	private static final String CENTER_LINE = "├ ";
	private static final String DEFAULT_LINE = "│ ";

	private static final String[] OMITTED_REQUEST_HEADER = {"**Omitted request header**"};
	private static final String[] OMITTED_REQUEST_BODY = {"**Omitted request body**"};
	private static final String[] OMITTED_RESPONSE_HEADER = {"**Omitted response header**"};
	private static final String[] OMITTED_RESPONSE_BODY = {"**Omitted response body**"};
	private static final String[] NO_REQUEST_BODY = {BODY_TAG + "No request body"};
	private static final String[] NO_RESPONSE_BODY = {BODY_TAG + "No response body"};
	
	
	private static void printLog(String tag, String content){
		Log.i(tag, content);
	}

	/**
	 * 对 lines 中的信息进行逐行打印
	 * @param tag TAG
	 * @param lines lines
	 * @param limitLineSize 为 {@code true} 时, 每行的信息长度不会超过260, 超过则自动换行
	 */
	private static void printLogLines(String tag, String[] lines, boolean limitLineSize){
		if (limitLineSize){
			for (String line : lines) {
				int lineLength = line.length();
				int MAX_LONG_SIZE = 260;
				for (int i = 0; i <= lineLength / MAX_LONG_SIZE; i++) {
					int start = i * MAX_LONG_SIZE;
					int end = (i + 1) * MAX_LONG_SIZE;
					end = end > line.length() ? line.length() : end;
					printLog(tag, DEFAULT_LINE + line.substring(start, end));
				}
			}
		} else {
			for (String line : lines){
				printLog(tag, DEFAULT_LINE + line);
			}
		}
	}
	
	@Override
	public void printRequestBasic(Request request) {
		String tag = getTag(true);
		String[] basicLines = new String[]{URL_TAG + request.url()};
		String[] methodLines = new String[]{METHOD_TAG + request.method()};
		printLog(tag, REQUEST_UP_LINE);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, methodLines, false);
		printLog(tag, END_LINE);
	}

	@Override
	public void printRequestBasicAndBody(Request request, String formatBodyStr) {
		String tag = getTag(true);
		String[] basicLines = new String[]{URL_TAG + request.url()};
		String[] methodLines = new String[]{METHOD_TAG + request.method()};
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_REQUEST_BODY : bodyStr.split(LINE_SEPARATOR);
		printLog(tag, REQUEST_UP_LINE);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, methodLines, false);
		printLogLines(tag, OMITTED_REQUEST_HEADER, false);
		printLogLines(tag, bodyLines, true);
		printLog(tag, END_LINE);
	}

	@Override
	public void printRequestBasicAndHeader(Request request) {
		String tag = getTag(true);
		String[] basicLines = new String[]{URL_TAG + request.url()};
		String[] methodLines = new String[]{METHOD_TAG + request.method()};
		String[] headerLines = formatRequestHeader(request);
		printLog(tag, REQUEST_UP_LINE);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, methodLines, false);
		printLogLines(tag, headerLines, true);
		printLogLines(tag, OMITTED_REQUEST_BODY, false);
		printLog(tag, END_LINE);
	}

	@Override
	public void printRequest(Request request, String formatBodyStr) {
		String tag = getTag(true);
		String[] basicLines = new String[]{URL_TAG + request.url()};
		String[] methodLines = new String[]{METHOD_TAG + request.method()};
		String[] headerLines = formatRequestHeader(request);
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_REQUEST_BODY : bodyStr.split(LINE_SEPARATOR);
		printLog(tag, REQUEST_UP_LINE);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, methodLines, false);
		printLogLines(tag, headerLines, true);
		printLogLines(tag, bodyLines, true);
		printLog(tag, END_LINE);
	}

	@Override
	public void printResponseBasic(long elapsedTime, Response response) {
		String tag = getTag(false);
		String[] basicLines = formatResponseBasicInfo(response);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		printLog(tag, RESPONSE_UP_LINE);
		printLogLines(tag, elaTimeLines, false);
		printLogLines(tag, basicLines, false);
		printLog(tag, END_LINE);
	}

	@Override
	public void printResponseBasicAndBody(long elapsedTime, Response response, String formatBodyStr) {
		String tag = getTag(false);
		String[] basicLines = formatResponseBasicInfo(response);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_RESPONSE_BODY : bodyStr.split(LINE_SEPARATOR);
		printLog(tag, RESPONSE_UP_LINE);
		printLogLines(tag, elaTimeLines, false);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, OMITTED_RESPONSE_HEADER, false);
		printLogLines(tag, bodyLines, true);
		printLog(tag, END_LINE);
	}

	@Override
	public void printResponseBasicAndHeader(long elapsedTime, Response response) {
		String tag = getTag(false);
		String[] basicLines = formatResponseBasicInfo(response);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] headerLines = formatResponseHeader(response);
		printLog(tag, RESPONSE_UP_LINE);
		printLogLines(tag, elaTimeLines, false);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, headerLines, true);
		printLogLines(tag, OMITTED_RESPONSE_BODY, false);
		printLog(tag, END_LINE);
	}

	@Override
	public void printResponse(long elapsedTime, Response response, String formatBodyStr) {
		String tag = getTag(false);
		String[] basicLines = formatResponseBasicInfo(response);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] headerLines = formatResponseHeader(response);
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_RESPONSE_BODY : bodyStr.split(LINE_SEPARATOR);
		printLog(tag, RESPONSE_UP_LINE);
		printLogLines(tag, elaTimeLines, false);
		printLogLines(tag, basicLines, false);
		printLogLines(tag, headerLines, true);
		printLogLines(tag, bodyLines, true);
		printLog(tag, END_LINE);
	}

	private static String[] formatRequestHeader(Request request) {
		String header = request.headers().toString();
		String log = (isEmpty(header) ? "" : HEADERS_TAG + formatHeader(header));
		return log.split(LINE_SEPARATOR);
	}

	private static String formatHeader(String header) {
		String[] headers = header.split(LINE_SEPARATOR);
		StringBuilder builder = new StringBuilder();
		String tag = "─ ";
		if (headers.length > 1) {
			for (int i = 0; i < headers.length; i++) {
				if (i == 0) {
					tag = CORNER_UP;
					builder.append(" ").append(tag);
				} else if (i == headers.length - 1) {
					tag = CORNER_BOTTOM;
					builder.append(getCountBlank(HEADERS_TAG.length() + 1)).append(tag);
				} else {
					tag = CENTER_LINE;
					builder.append(getCountBlank(HEADERS_TAG.length() + 1)).append(tag);
				}
				builder.append(headers[i]).append(N);
			}
		} else {
			for (String item : headers) {
				builder.append(" ").append(tag).append(item).append(N);
			}
		}
		return builder.toString();
	}
	
	private static String[] formatResponseBasicInfo(Response response){
		boolean isSuccess = response.isSuccessful();
		int code = response.code();
		String message = response.message();
		String log = STATUS_TAG + (isSuccess ? "success" : "failed") + " / " + code + " / " + message;
		return new String[]{log};
	}
	
	private static String[] formatResponseHeader(Response response){
		String header = response.headers().toString();
		String log = (isEmpty(header) ? "" : HEADERS_TAG + formatHeader(header));
		return log.split(LINE_SEPARATOR);
	}
	

	private static boolean isEmpty(String line) {
		return TextUtils.isEmpty(line) || N.equals(line) || T.equals(line) || TextUtils.isEmpty(line.trim());
	}

	private static String getTag(boolean isRequest) {
		if (isRequest) {
			return TAG + "-Request";
		} else {
			return TAG + "-Response";
		}
	}
	
	private static String getCountBlank(int count){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < count; i++){
			str.append(" ");
		}
		return str.toString();
	}
	
}

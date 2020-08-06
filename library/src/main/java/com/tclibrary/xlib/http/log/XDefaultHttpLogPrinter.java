package com.tclibrary.xlib.http.log;

import android.text.TextUtils;
import android.util.Log;

import com.tclibrary.xlib.utils.SystemUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FunTc on 2020/05/15.
 * single tag日志
 */
public class XDefaultHttpLogPrinter implements IHttpLogPrinter {

	private static final String TAG = "HttpLog";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;
	private static final String PLACEHOLDER    = " ";

	private static final String N = "\n";
	private static final String T = "\t";
	private static final String REQUEST_UP_LINE =
			"┌────── Request ─────────────────────────────────────────────────────────────────────────────────────────────────";
	private static final String END_LINE =
			"└────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
	private static final String RESPONSE_UP_LINE =
			"┌────── Response ────────────────────────────────────────────────────────────────────────────────────────────────";
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

	private static final String OMITTED_REQUEST_HEADER = "**Omitted request header**";
	private static final String OMITTED_REQUEST_BODY = "**Omitted request body**";
	private static final String OMITTED_RESPONSE_HEADER = "**Omitted response header**";
	private static final String OMITTED_RESPONSE_BODY = "**Omitted response body**";
	private static final String[] NO_REQUEST_BODY = {BODY_TAG + "No request body"};
	private static final String[] NO_RESPONSE_BODY = {BODY_TAG + "No response body"};
	
	
	private static void printLog(String tag, String content) {
		/* 单个日志超过最大长度就分段打印，Log打印的最大长度为4k Byte，
		   考虑到有中文的情况(GBK编码中一个汉字占2个字节，UTF-8编码中一个汉字占3个字节)，Logcat一般都为UTF-8编码 */
		int MAX_LEN = 3 * 1024;
		int len = content.length();
		int count = (len - END_LINE.length())/ MAX_LEN;
		if (count > 0) {
			int start = 0;
			for (int i = 0; i < count; i++) {
				int end = content.lastIndexOf(LINE_SEPARATOR, start + MAX_LEN);
				if (i > 0) {
					Log.i(tag, " " + content.substring(start, end));
				} else {
					Log.i(tag, content.substring(start, end));
				}
				start = end;
			}
			if (start != len - END_LINE.length()) {
				Log.i(tag, " " + content.substring(start, len));
			}
		} else {
			Log.i(tag, content);
		}
	}

	private static String processSingleTagMsg(boolean isRequest, String... contentLines) {
		final int maxLen = (int) (END_LINE.length() * 1.6f);
		StringBuilder sb = new StringBuilder();
		sb.append(PLACEHOLDER).append(LINE_SEPARATOR);
		if (isRequest) {
			sb.append(REQUEST_UP_LINE).append(LINE_SEPARATOR);
		} else {
			sb.append(RESPONSE_UP_LINE).append(LINE_SEPARATOR);
		}
		
		for (String line : contentLines) {
			for (String subLine: line.split(LINE_SEPARATOR)) {
				if (subLine.length() <= maxLen) {
					sb.append(DEFAULT_LINE).append(subLine).append(LINE_SEPARATOR);
				} else {
					int len = subLine.length();
					float fc = (float)len / maxLen;
					int count = (int)fc + 1;
					for (int i = 0; i < count; i++) {
						int start = i * maxLen;
						int end = (i + 1) * maxLen;
						if (end > len) {
							end = len;
						}
						sb.append(DEFAULT_LINE).append(subLine.substring(start, end)).append(LINE_SEPARATOR);
					}
				}
			}
		}
		sb.append(END_LINE);
		return sb.toString();
	}
	
	@Override
	public void printRequestBasic(Request request) {
		String tag = getTag(true);
		String urlLine = URL_TAG + request.url();
		String methodLine = METHOD_TAG + request.method();
		printLog(tag, processSingleTagMsg(true, urlLine, methodLine));
	}

	@Override
	public void printRequestBasicAndBody(Request request, String formatBodyStr) {
		String tag = getTag(true);
		String[] basicLines = {
				URL_TAG + request.url(),
				METHOD_TAG + request.method(),
				OMITTED_REQUEST_HEADER
		};
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_REQUEST_BODY : bodyStr.split(LINE_SEPARATOR);
		String[] contents = SystemUtils.concat(basicLines, bodyLines);
		printLog(tag, processSingleTagMsg(true, contents));
	}

	@Override
	public void printRequestBasicAndHeader(Request request) {
		String tag = getTag(true);
		String[] basicLines = {
				URL_TAG + request.url(),
				METHOD_TAG + request.method(),
		};
		String[] headerLines = formatRequestHeader(request);
		basicLines = SystemUtils.concat(basicLines, headerLines);
		String[] contents = SystemUtils.concat(basicLines, new String[] {OMITTED_REQUEST_BODY});
		printLog(tag, processSingleTagMsg(true, contents));
	}

	@Override
	public void printRequest(Request request, String formatBodyStr) {
		String tag = getTag(true);
		String[] basicLines = {
				URL_TAG + request.url(),
				METHOD_TAG + request.method()
		};
		String[] headerLines = formatRequestHeader(request);
		basicLines = SystemUtils.concat(basicLines, headerLines);
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_REQUEST_BODY : bodyStr.split(LINE_SEPARATOR);
		String[] contents = SystemUtils.concat(basicLines, bodyLines);
		printLog(tag, processSingleTagMsg(true, contents));
	}

	@Override
	public void printResponseBasic(long elapsedTime, Response response) {
		String tag = getTag(false);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] basicLines = formatResponseBasicInfo(response);
		String[] contents = SystemUtils.concat(elaTimeLines, basicLines);
		printLog(tag, processSingleTagMsg(false, contents));
	}

	@Override
	public void printResponseBasicAndBody(long elapsedTime, Response response, String formatBodyStr) {
		String tag = getTag(false);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] basicLines = formatResponseBasicInfo(response);
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_RESPONSE_BODY : bodyStr.split(LINE_SEPARATOR);
		
		String[] contents = SystemUtils.concat(elaTimeLines, basicLines);
		contents = SystemUtils.concat(contents, new String[] {OMITTED_RESPONSE_HEADER});
		contents = SystemUtils.concat(contents, bodyLines);

		printLog(tag, processSingleTagMsg(false, contents));
	}

	@Override
	public void printResponseBasicAndHeader(long elapsedTime, Response response) {
		String tag = getTag(false);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] basicLines = formatResponseBasicInfo(response);
		String[] headerLines = formatResponseHeader(response);
		
		String[] contents = SystemUtils.concat(elaTimeLines, basicLines);
		contents = SystemUtils.concat(contents, headerLines);
		contents = SystemUtils.concat(contents, new String[] {OMITTED_RESPONSE_BODY});

		printLog(tag, processSingleTagMsg(false, contents));
	}

	@Override
	public void printResponse(long elapsedTime, Response response, String formatBodyStr) {
		String tag = getTag(false);
		String[] elaTimeLines = new String[]{ELAPSED_TAG + elapsedTime + "ms"};
		String[] basicLines = formatResponseBasicInfo(response);
		String[] headerLines = formatResponseHeader(response);
		String bodyStr = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + formatBodyStr;
		String[] bodyLines = TextUtils.isEmpty(formatBodyStr) ? NO_RESPONSE_BODY : bodyStr.split(LINE_SEPARATOR);
		
		String[] contents = SystemUtils.concat(elaTimeLines, basicLines);
		contents = SystemUtils.concat(contents, headerLines);
		contents = SystemUtils.concat(contents, bodyLines);

		printLog(tag, processSingleTagMsg(false, contents));
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
		String log = STATUS_TAG + (isSuccess ? "success" : "failed") + " | " + code + " | " + message;
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

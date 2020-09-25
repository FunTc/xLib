package com.tclibrary.xlib.http.log;

import android.text.TextUtils;

import com.tclibrary.xlib.utils.OkHttpUtils;
import com.tclibrary.xlib.utils.SystemUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by FunTc on 2018/10/23.
 *
 */
public class HttpLoggerInterceptor implements Interceptor {

    public enum Level{
        /** 不打印log */
        NONE,
        /** 只打印最简单的信息（请求体、返回码，是否成功，请求耗时）*/
        BASIC,
        /** 只打主要信息（在BASIC的级别上加入：显示body内容）*/
        BODY,
        /** 只打主要信息（在BASIC的级别上加入：显示headers）*/
        HEADERS,
        /** 所有数据全部打印 */
        ALL
    }

    private Level printLevel;
    private IHttpLogPrinter mLogPrinter;

    public HttpLoggerInterceptor(IHttpLogPrinter logPrinter, Level level){
        mLogPrinter = logPrinter;
        printLevel = level;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (printLevel == Level.NONE) {
            return chain.proceed(request);
        }
        Request newRequest = request.newBuilder().build();
        if (printLevel == Level.BASIC){
            mLogPrinter.printRequestBasic(newRequest);
        } else if (printLevel == Level.HEADERS) {
            mLogPrinter.printRequestBasicAndHeader(newRequest);
        } else if (printLevel == Level.BODY) {
            mLogPrinter.printRequestBasicAndBody(newRequest, getRequestBodyString(newRequest));
        } else {
            mLogPrinter.printRequest(newRequest, getRequestBodyString(newRequest));
        }

        long startTime = System.nanoTime();
        Response response = chain.proceed(request);
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        Response newResponse = response.newBuilder().build();
        String bodyString = getResponseBodyString(newResponse);

        if (printLevel == Level.BASIC){
            mLogPrinter.printResponseBasic(elapsedTime, newResponse);
        } else if (printLevel == Level.HEADERS) {
            mLogPrinter.printResponseBasicAndHeader(elapsedTime, newResponse);
        } else if (printLevel == Level.BODY) {
            mLogPrinter.printResponseBasicAndBody(elapsedTime, newResponse, bodyString);
        } else {
            mLogPrinter.printResponse(elapsedTime, newResponse, bodyString);
        }
        return response;
    }


    private String getRequestBodyString(Request request) {
        String bodyStr = "";
        RequestBody body = request.body();
        if (body!= null && isCanParse(body.contentType())){
            try {
                Buffer requestBuffer = new Buffer();
                body.writeTo(requestBuffer);
                Charset charset = StandardCharsets.UTF_8;
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                }
                bodyStr = SystemUtils.jsonFormat(URLDecoder.decode(requestBuffer.readString(Objects.requireNonNull(charset)), convertCharset(charset)));
            } catch (IOException e) {
                e.printStackTrace();
                bodyStr = "{\"error\": \"" + e.getMessage() + "\"}";
            }
        }
        return bodyStr;
    }

    private String getResponseBodyString(Response response){
        String bodyStr = "";
        ResponseBody body = response.body();
        if (body != null && isCanParse(body.contentType())){
            try {
                BufferedSource source = body.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.getBuffer();
                //获取content的压缩类型
                String encoding = response.headers().get("Content-Encoding");
                Buffer clone = buffer.clone();
                //解析response content
                bodyStr = parseContent(body, encoding, clone);
            } catch (IOException e) {
                e.printStackTrace();
                bodyStr = "{\"error\": \"" + e.getMessage() + "\"}";
            }

            if (!TextUtils.isEmpty(bodyStr)){
                bodyStr = OkHttpUtils.isJson(body.contentType()) ? SystemUtils.jsonFormat(bodyStr)
                        : OkHttpUtils.isXml(body.contentType()) ? SystemUtils.xmlFormat(bodyStr) : bodyStr;
            }
        }
        return bodyStr;
    }

    /**
     * 解析服务器响应的内容
     */
    private String parseContent(ResponseBody responseBody, String encoding, Buffer clone) {
        Charset charset = StandardCharsets.UTF_8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        if (charset == null) return "";
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {//content使用gzip压缩
            return OkHttpUtils.decompressForGzip(clone.readByteArray(), convertCharset(charset));
        } else if (encoding != null && encoding.equalsIgnoreCase("zlib")) {//content使用zlib压缩
            return OkHttpUtils.decompressToStringForZlib(clone.readByteArray(), convertCharset(charset));
        } else {//content没有被压缩
            return clone.readString(charset);
        }
    }

    private String convertCharset(Charset charset) {
        String s = charset.toString();
        int i = s.indexOf("[");
        if (i == -1)
            return s;
        return s.substring(i + 1, s.length() - 1);
    }

    /**
     * 是否可以解析
     */
    private boolean isCanParse(MediaType mediaType) {
        return OkHttpUtils.isText(mediaType) || OkHttpUtils.isPlain(mediaType)
                || OkHttpUtils.isJson(mediaType) || OkHttpUtils.isForm(mediaType)
                || OkHttpUtils.isHtml(mediaType) || OkHttpUtils.isXml(mediaType);
    }

}

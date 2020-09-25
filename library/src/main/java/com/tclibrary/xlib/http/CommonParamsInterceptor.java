package com.tclibrary.xlib.http;


import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.tclibrary.xlib.http.fileload.FileRequestBody;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by FunTc on 2018/10/25.
 * 处理请求，添加公共参数
 */
public class CommonParamsInterceptor implements Interceptor {

    @NonNull protected final IRequestParamsHandler mRequestParamsHandler;

    public CommonParamsInterceptor(@NonNull IRequestParamsHandler requestParamsHandler){
        mRequestParamsHandler = requestParamsHandler;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request newRequest;
        if (oldRequest.method().equals("POST")) {
            RequestBody oldBody = oldRequest.body();
            RequestBody newRequestBody;
            if (oldBody instanceof FormBody) {
                newRequestBody = handleFormBody((FormBody) oldBody);
            } else if (oldBody instanceof MultipartBody) {
                newRequestBody = handleMultipartBody((MultipartBody) oldBody);
            } else if (oldBody instanceof FileRequestBody) {
                newRequestBody = handleFileRequestBody((FileRequestBody) oldBody);
            } else if (isHandleCustomRequestBody(oldBody)) {
                newRequestBody = handleCustomRequestBody(oldBody);
            } else {
                newRequestBody = handleOtherRequestBody(oldBody);
            }
            newRequest = oldRequest.newBuilder().post(newRequestBody).build();
        } else if (oldRequest.method().equals("GET")) {
            newRequest = handleGetRequest(oldRequest);
        } else {
            newRequest = oldRequest;
        }
		
		/*Request newRequest = newRequestBuilder
				.addHeader("Accept", "application/json")
				.addHeader("Accept-Language", "zh")
				.build();*/

        return chain.proceed(newRequest);
    }

    protected RequestBody handleFormBody(FormBody body) {
        Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
        //拿到源请求参数，添加要公共参数中
        for (int i = 0; i < body.size(); i++) {
            requestParams.put(body.name(i), body.value(i));
        }
        //处理请求参数，比如对所有参数做一个签名字段
        mRequestParamsHandler.handleParams(requestParams);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()){
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        return formBodyBuilder.build();
    }

    private Pair<String, String> getMultipartBodyFormData(MultipartBody.Part part) {
        RequestBody body = part.body();
        Headers headers = part.headers();
        String key = "", value = null;
        if (headers != null) {
            key = headers.value(0).split("\"")[1];
        }
        try {
            Field[] fields = body.getClass().getDeclaredFields();
            for (Field field : fields) {
                Class<?> clz = field.getType();
                if (clz == byte[].class) {
                    field.setAccessible(true);
                    byte[] cb = (byte[]) field.get(body);
                    if (cb != null) {
                        value = new String(cb, StandardCharsets.UTF_8);
                    }
                    break;
                }
            }
        } catch (IllegalAccessException ignored) { }
        return new Pair<>(key, value);
    }

    protected RequestBody handleMultipartBody(MultipartBody body) {
        Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (MultipartBody.Part part : body.parts()){
            if (part.body().contentType() != null) {
                fileParts.add(part);
            } else {
                Pair<String, String> pair = getMultipartBodyFormData(part);
                requestParams.put(pair.first, pair.second);
            }
        }
        mRequestParamsHandler.handleParams(requestParams);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : requestParams.entrySet()){
            multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (MultipartBody.Part part : fileParts) {
            multipartBodyBuilder.addPart(part);
        }
        return multipartBodyBuilder.build();
    }

    protected RequestBody handleFileRequestBody(FileRequestBody body) {
        RequestBody contentBody = body.getRawRequestBody();
        if (contentBody instanceof MultipartBody) {
            RequestBody newBody = handleMultipartBody((MultipartBody) contentBody);
            body.setRequestBody(newBody);
        }
        return body;
    }

    protected RequestBody handleOtherRequestBody(RequestBody body) {
        Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
        String oldJsonParams = bodyToString(body);
        Map<String, String> originalParams = GsonUtils.fromJson(oldJsonParams, new TypeToken<Map<String, String>>(){}.getType());
        if (originalParams != null) requestParams.putAll(originalParams);
        mRequestParamsHandler.handleParams(requestParams);
        String newJsonParams = GsonUtils.toJson(requestParams);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newJsonParams);
    }

    private static String bodyToString(final RequestBody body) {
        try {
            final Buffer buffer = new Buffer();
            if (body != null)
                body.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected boolean isHandleCustomRequestBody(RequestBody body) {
        return false;
    }

    protected RequestBody handleCustomRequestBody(RequestBody body) {
        return body;
    }


    protected Request handleGetRequest(Request request) {
        Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
        HttpUrl httpUrl = request.url();
        for (int i = 0; i < httpUrl.querySize(); i++){
            requestParams.put(httpUrl.queryParameterName(i), httpUrl.queryParameterValue(i));
        }
        mRequestParamsHandler.handleParams(requestParams);
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()){
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return request.newBuilder().url(urlBuilder.build()).build();
    }


}

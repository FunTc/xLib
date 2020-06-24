package com.tclibrary.xlib.http;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import okhttp3.FormBody;
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
	
	@NonNull private IRequestParamsHandler mRequestParamsHandler;
	
	public CommonParamsInterceptor(@NonNull IRequestParamsHandler requestParamsHandler){
		mRequestParamsHandler = requestParamsHandler;
	}

	@NonNull
	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Request oldRequest = chain.request();
		Request.Builder newRequestBuilder;
		if (oldRequest.method().equals("POST")) {
			RequestBody oldBody = oldRequest.body();
			if (oldBody instanceof FormBody) {
				Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
				//拿到源请求参数，添加要公共参数中
				for (int i = 0; i < ((FormBody) oldBody).size(); i++) {
					requestParams.put(((FormBody) oldBody).encodedName(i), ((FormBody) oldBody).encodedValue(i));
				}
				//处理请求参数，比如对所有参数做一个签名字段
				mRequestParamsHandler.handleParams(requestParams);
				FormBody.Builder formBodyBuilder = new FormBody.Builder();
				for (Map.Entry<String, String> entry : requestParams.entrySet()){
					formBodyBuilder.add(entry.getKey(), entry.getValue());
				}
				newRequestBuilder = oldRequest.newBuilder().post(formBodyBuilder.build());
				
			} else if (oldBody instanceof MultipartBody) {
				Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
				MultipartBody oldMultipartBody = (MultipartBody) oldBody;
				List<MultipartBody.Part> oldOtherPartList = new ArrayList<>();
				for (MultipartBody.Part part : oldMultipartBody.parts()){
					RequestBody body = part.body();
					if (body instanceof FormBody){
						//拿出所有FormBody中的请求参数，然后放入requestParams中
						for (int i = 0; i < ((FormBody) body).size(); i++) {
							requestParams.put(((FormBody) body).encodedName(i), ((FormBody) body).encodedValue(i));
						}
					} else {
						oldOtherPartList.add(part);
					}
				}
				mRequestParamsHandler.handleParams(requestParams);
				MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
				multipartBodyBuilder.setType(MultipartBody.FORM);
				for (Map.Entry<String, String> entry : requestParams.entrySet()){
					multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
				}
				for (MultipartBody.Part part : oldOtherPartList) {
					multipartBodyBuilder.addPart(part);
				}
				newRequestBuilder = oldRequest.newBuilder().post(multipartBodyBuilder.build());
				
			} else {
				Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
				Buffer buffer = new Buffer();
				Objects.requireNonNull(oldBody).writeTo(buffer);
				String oldJsonParams = buffer.readUtf8();
				Map<String, String> originalParams = GsonUtils.fromJson(oldJsonParams, new TypeToken<Map<String, String>>(){}.getType());
				requestParams.putAll(originalParams);
				mRequestParamsHandler.handleParams(requestParams);
				String newJsonParams = GsonUtils.toJson(requestParams);
				newRequestBuilder = oldRequest.newBuilder().post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newJsonParams));
			}
		} else {
			Map<String, String> requestParams = mRequestParamsHandler.getCommonParams();
			HttpUrl httpUrl = oldRequest.url();
			for (int i = 0; i < httpUrl.querySize(); i++){
				requestParams.put(httpUrl.queryParameterName(i), httpUrl.queryParameterValue(i));
			}
			mRequestParamsHandler.handleParams(requestParams);
			HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
			for (Map.Entry<String, String> entry : requestParams.entrySet()){
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
			newRequestBuilder = oldRequest.newBuilder().url(urlBuilder.build());
		}
		
		/*Request newRequest = newRequestBuilder
				.addHeader("Accept", "application/json")
				.addHeader("Accept-Language", "zh")
				.build();*/

		return chain.proceed(newRequestBuilder.build());
	}

	private static String bodyToString(final RequestBody request) {
		try {
			final RequestBody copy = request;
			final Buffer buffer = new Buffer();
			if (copy != null)
				copy.writeTo(buffer);
			else
				return "";
			return buffer.readUtf8();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}

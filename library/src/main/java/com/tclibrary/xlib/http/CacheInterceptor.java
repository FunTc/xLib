package com.tclibrary.xlib.http;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FunTc on 2018/10/30.
 */
public class CacheInterceptor implements Interceptor {
	
	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Request request = chain.request();
		boolean netAvailable = NetworkUtils.isConnected();
		if (netAvailable) {	//网络可用 强制从网络获取数据
			request = request.newBuilder()
					.cacheControl(CacheControl.FORCE_NETWORK)
					.build();
		} else { //网络不可用 从缓存获取
			request = request.newBuilder()
					.cacheControl(CacheControl.FORCE_CACHE)
					.build();
		}
		Response response = chain.proceed(request);
		if (netAvailable) {
			response = response.newBuilder()
					.removeHeader("Pragma")
					.header("Cache-Control", "public, max-age=" + 60 * 60)	//有网络时 设置缓存超时时间1个小时
					.build();
		} else {
			response = response.newBuilder()
					.removeHeader("Pragma")
					.header("Cache-Control", "public, only-if-cached, max-stale=" + 3 * 24 * 60 * 60)	//无网络时，设置超时为3天
					.build();
		}
		return response;
	}
}

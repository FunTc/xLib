package com.tclibrary.xlib.http;

import com.tclibrary.xlib.BuildConfig;
import com.tclibrary.xlib.XApplication;
import com.tclibrary.xlib.http.log.HttpLoggerInterceptor;
import com.tclibrary.xlib.http.log.XDefaultHttpLogPrinter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by FunTc on 2018/10/23.
 */
public class DefaultHttpConfig implements IHttpConfig {

    @Override
    public void onOkHttpClientConfig(@NonNull OkHttpClient.Builder builder) {
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(new Cache(new File(XApplication.getAppContext().getCacheDir(), "httpCache"), 10 * 1024 * 1024))
                .addInterceptor(new CacheInterceptor())
                .addInterceptor(new CommonParamsInterceptor(new RequestParamsHandlerImpl()));
        if (BuildConfig.DEBUG){
            builder.addInterceptor(new HttpLoggerInterceptor(new XDefaultHttpLogPrinter(), HttpLoggerInterceptor.Level.ALL));
        }
    }

    @Override
    public void onRetrofitConfig(@NonNull Retrofit.Builder builder) {
        builder.addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

}

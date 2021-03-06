package com.tclibrary.xlib.http;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by FunTc on 2018/10/23.
 */
public interface IHttpConfig {

    void onOkHttpClientConfig(@NonNull OkHttpClient.Builder builder);

    void onRetrofitConfig(@NonNull Retrofit.Builder builder);

}

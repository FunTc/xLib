package com.tclibrary.xlib.http.processor;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonParser;
import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.http.HttpManager;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FunTc on 2020/05/15.
 */
public class OkHttpProcessor extends BaseHttpProcessor {
    
    private String mApi;
    private Class<?> mApiClz;
    private Class<?> mModelClz;
    private Call mCallTask;
    private boolean isCanceled;
    
    public OkHttpProcessor(@NonNull String api) {
        mApi = api;
    }
    
    public OkHttpProcessor(@NonNull String api, Class<?> apiClz) {
        this(api);
        mApiClz = apiClz;
    }
    
    public OkHttpProcessor model(@NonNull Class<?> model) {
        mModelClz = model;
        return this;
    }
    
    @Override
    protected void onProcessInternal(@NonNull Event event) throws Exception {
        RequestParams requestParams = event.findValue(RequestParams.class);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (requestParams != null) {
            Map<String, Object> mapParams = getMapFromParams(requestParams.getParams());
            List<Pair<String, Object>> pairParams = getPairsFromParams(requestParams.getParams());
            for (Map.Entry<String, Object> entry: mapParams.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue().toString());
            }
            for (Pair<String, Object> pair: pairParams) {
                formBodyBuilder.add(pair.first, pair.second.toString());
            }
        }
        String serviceURL = HttpManager.instance().getServiceURL(mApiClz);
        if (serviceURL == null) {
            throw new Exception("cannot find base url");
        }
        Request request = new Request.Builder().url(serviceURL + mApi).post(formBodyBuilder.build()).build();
        OkHttpClient client = HttpManager.instance().getOkHttpClient();
        mCallTask = client.newCall(request);
        Response response = mCallTask.execute();
        if (isCanceled) {
            event.setIsSuccess(false);
        } else {
            if (response.isSuccessful()) {
                String result = response.body().string();
                event.addProcessedValue(GsonUtils.getGson().fromJson(result, mModelClz));
                event.addProcessedValue(new JsonParser().parse(result).getAsJsonObject());
            }
            event.setIsSuccess(response.isSuccessful());
        }
    }

    @Override
    public void cancel(boolean mayInterruptIfRunning) {
        isCanceled = true;
        if (mayInterruptIfRunning && mCallTask != null) {
            mCallTask.cancel();
        }
    }
    
}

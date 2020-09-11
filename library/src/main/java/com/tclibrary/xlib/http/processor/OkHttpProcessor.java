package com.tclibrary.xlib.http.processor;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.http.HttpManager;
import com.tclibrary.xlib.http.IResult;
import com.tclibrary.xlib.http.XHttpException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FunTc on 2020/05/15.
 */
public class OkHttpProcessor extends BaseHttpProcessor {
    
    protected String mApi;
    protected Class<?> mApiClz;
    protected Call mCallTask;
    protected boolean isCanceled;
    
    protected Class<? extends IResult> mModelClz;
    protected Type mModelType;
    
    public OkHttpProcessor(@NonNull String api, @NonNull Class<? extends IResult> modelClz) {
        this(api, null, modelClz);
    }
    
    public OkHttpProcessor(@NonNull String api, Class<?> apiClz, @NonNull Class<? extends IResult> modelClz) {
        mApi = api;
        mApiClz = apiClz;
        mModelClz = modelClz;
    }

    public OkHttpProcessor(@NonNull String api, @NonNull Type modelType) {
        this(api, null, modelType);
    }

    public OkHttpProcessor(@NonNull String api, Class<?> apiClz, @NonNull Type modelType) {
        mApi = api;
        mApiClz = apiClz;
        mModelType = modelType;
    }
    
    @Override
    protected void onProcessInternal(@NonNull Event event) throws Exception {
        RequestBody body = buildRequestBody(event);
        String serviceURL = HttpManager.instance().getServiceURL(mApiClz);
        if (serviceURL == null) throw new Exception("cannot find base url");
        Response response = doRequest(serviceURL + mApi, body);
        if (isCanceled) {
            event.setIsSuccess(false);
        } else if (response.isSuccessful()) {
            handleResult(event, response.body().string());
        } else {
            throw new XHttpException(response.code(), response.message());
        }
    }
    
    protected RequestBody buildRequestBody(@NonNull Event event) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (event.getValues() != null) {
            Map<String, Object> mapParams = getMapFromParams(event.getValues());
            List<Pair<String, Object>> pairParams = getPairsFromParams(event.getValues());
            if (mapParams.size() != 0) {
                for (Map.Entry<String, Object> entry: mapParams.entrySet()) {
                    Object value = entry.getValue();
                    formBodyBuilder.add(entry.getKey(), value == null ? "" : value.toString());
                }
            } else if (pairParams.size() != 0) {
                for (Pair<String, Object> pair: pairParams) {
                    if (pair.first == null) continue;
                    formBodyBuilder.add(pair.first, pair.second == null ? "" : pair.second.toString());
                }
            }
        }
        return formBodyBuilder.build();
    }
    
    protected Response doRequest(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder().url(url).post(body).build();
        OkHttpClient client = HttpManager.instance().getOkHttpClient();
        mCallTask = client.newCall(request);
        return mCallTask.execute();
    }
    
    protected void handleResult(Event event, String result) throws Exception{
        JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
        IResult ir;
        if (mModelClz != null) {
            ir = GsonUtils.getGson().fromJson(result, mModelClz);
        } else if (mModelType != null) {
            ir = GsonUtils.getGson().fromJson(result, mModelType);
        } else {
            ir = GsonUtils.getGson().fromJson(result, IResult.class);
        }
        event.setIsSuccess(ir.isSuccess());
        event.addProcessedValue(ir);
        event.addProcessedValue(jo);
    }

    @Override
    public void cancel(boolean mayInterruptIfRunning) {
        isCanceled = true;
        if (mayInterruptIfRunning && mCallTask != null) {
            mCallTask.cancel();
        }
    }
    
}

package com.tclibrary.xlib.http.fileload;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by FunTc on 2020/06/22.
 */
public abstract class FileLoadCallback<T> implements FileLoadProgressListener, Callback {
    
    protected Type type;
    
    public FileLoadCallback() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        this.type = $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        
    }

    @Override
    public final void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.getBuffer();
        String data = buffer.clone().readString(StandardCharsets.UTF_8);
        T result = GsonUtils.fromJson(data, this.type);
        onSuccess(call, response, result);
    }

    @Override
    public void onProgress(long current, long total, boolean isDone) {
        
    }
    
    public abstract void onSuccess(@NonNull Call call, @NonNull Response response, @NonNull T result);
}

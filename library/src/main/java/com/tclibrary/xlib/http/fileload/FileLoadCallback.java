package com.tclibrary.xlib.http.fileload;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by FunTc on 2020/06/22.
 */
public abstract class FileLoadCallback<T> implements FileLoadProgressListener, Callback {

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        
    }

    @Override
    public final void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        String resultStr = response.body().string();
        T result = GsonUtils.getGson().fromJson(resultStr, new TypeToken<T>(){}.getType());
        onSuccess(call, response, result);
    }

    @Override
    public void onProgress(long current, long total, boolean isDone) {
        
    }
    
    public abstract void onSuccess(@NonNull Call call, @NonNull Response response, @NonNull T result);
}

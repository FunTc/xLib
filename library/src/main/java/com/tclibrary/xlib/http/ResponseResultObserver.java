package com.tclibrary.xlib.http;

import android.accounts.NetworkErrorException;
import android.net.ParseException;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by FunTc on 2018/10/26.
 */
public abstract class ResponseResultObserver<T> implements Observer<T> {

    protected abstract void onSuccess(@NonNull T result);

    protected abstract void onFailed(@NonNull Exception e);

    protected void onStartRequest() { }

    protected void onEndRequest() { }

    @Override
    public void onSubscribe(Disposable d) {
        onStartRequest();
    }

    @Override
    public void onNext(T t) {
        onEndRequest();
        if (t instanceof IResult) {
            IResult result = (IResult) t;
            if (result.isSuccess()) {
                onSuccess(t);
            } else {
                onFailed(new ResponseResultException(result.getCode(), result.getMessage()));
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ConnectException
                || e instanceof UnknownHostException
                || e instanceof HttpException
                || e instanceof InterruptedIOException
                || e instanceof TimeoutException
                || e instanceof NetworkErrorException) { //网络错误
            LogUtils.e("网络错误", e);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) { //解析错误
            LogUtils.e("解析出错", e);
        } else {
            LogUtils.e("其他错误", e);
        }
        onFailed((Exception) e);
        onEndRequest();
    }

    @Override
    public void onComplete() { }

}

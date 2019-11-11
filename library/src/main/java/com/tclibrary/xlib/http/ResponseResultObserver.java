package com.tclibrary.xlib.http;

import android.accounts.NetworkErrorException;
import android.net.ParseException;
import android.support.annotation.NonNull;

import com.google.gson.JsonParseException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by FunTc on 2018/10/26.
 */
public abstract class ResponseResultObserver<T extends IResponseResult> implements Observer<T> {

	protected abstract void onSuccess(@NonNull T result);

	protected abstract void onFailed(@NonNull Exception e);

	protected void onStartRequest() { }

	protected void onEndRequest() { }

	@Override
	public final void onSubscribe(Disposable d) {
		onStartRequest();
	}

	@Override
	public final void onNext(T t) {
		onEndRequest();
		if (t.isRequestSuccess()) {
			onSuccess(t);
		} else {
			onFailed(new ResponseResultException(t.getResponseCode(), t.getResponseMessage()));
		}
	}

	@Override
	public final void onError(Throwable e) {
		if (e instanceof ConnectException
				|| e instanceof UnknownHostException
				|| e instanceof HttpException
				|| e instanceof InterruptedIOException
				|| e instanceof TimeoutException
				|| e instanceof NetworkErrorException) { //网络错误
			Logger.e(e, "网络错误");
		} else if (e instanceof JsonParseException
				|| e instanceof JSONException
				|| e instanceof ParseException) { //解析错误
			Logger.e(e, "解析出错");
		} else {
			Logger.e(e, "其他错误");
		}
		onFailed((Exception) e);
		onEndRequest();
	}

	@Override
	public final void onComplete() { }

}

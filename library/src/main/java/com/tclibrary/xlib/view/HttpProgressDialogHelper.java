package com.tclibrary.xlib.view;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by FunTc on 2018/10/29.
 */
public class HttpProgressDialogHelper {

	private static final class InstanceHolder {
		private static final HttpProgressDialogHelper INSTANCE = new HttpProgressDialogHelper();
	}

	public static HttpProgressDialogHelper instance() {
		return InstanceHolder.INSTANCE;
	}

	private final static long SHOW_TIMEOUT	=	20_000;
	private IProgressView mIProgressView;
	private Handler mMainHandler;
	
	private HttpProgressDialogHelper() {
		mMainHandler = new Handler(Looper.getMainLooper());
		mIProgressView = new DefaultHttpProgressViewHolder();
	}
	
	public void init(IProgressView progressView){
		mIProgressView = progressView;
	}

	public void show(long timeout) {
		show(null, timeout);
	}
	
	public void show(final CharSequence msg, long timeout) {
		if (isMainThread()){
			mIProgressView.show(msg);
		} else {
			mMainHandler.post(() -> mIProgressView.show(msg));
		}
		mMainHandler.postDelayed(this::dismiss, timeout);
	}
	
	public void show() {
		show(SHOW_TIMEOUT);
	}

	public void show(final CharSequence msg) {
		show(msg, SHOW_TIMEOUT);
	}

	public boolean isShowing() {
		return mIProgressView.isShowing();
	}

	public void dismiss() {
		if (isMainThread()){
			mIProgressView.dismiss();
		} else {
			mMainHandler.post(() -> mIProgressView.dismiss());
		}
	}

	private boolean isMainThread(){
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

}
package com.tclibrary.xlib.view;

import android.content.Context;
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

	public synchronized void show(long timeout) {
		show(null, null, timeout);
	}

	public synchronized void show(Context context, long timeout) {
		show(context, null, timeout);
	} 
	
	public synchronized void show(final CharSequence msg, long timeout) {
		show(null, msg, timeout);
	}

	public synchronized void show(Context context, final CharSequence msg, long timeout) {
		if (isShowing()) dismiss();
		if (isMainThread()){
			mIProgressView.show(context, msg);
		} else {
			mMainHandler.post(() -> mIProgressView.show(msg));
		}
		mMainHandler.postDelayed(autoDismissRunnable, timeout);
	}
	
	public synchronized void show() {
		show(SHOW_TIMEOUT);
	}
	
	public synchronized void show(Context context) {
		show(context, SHOW_TIMEOUT);
	}

	public synchronized void show(final CharSequence msg) {
		show(msg, SHOW_TIMEOUT);
	}

	public synchronized void show(Context context, final CharSequence msg) {
		show(context, msg, SHOW_TIMEOUT);
	}

	public boolean isShowing() {
		return mIProgressView.isShowing();
	}

	public synchronized void dismiss() {
		mMainHandler.removeCallbacks(autoDismissRunnable);
		if (isMainThread()){
			mIProgressView.dismiss();
		} else {
			mMainHandler.post(() -> mIProgressView.dismiss());
		}
	}

	private boolean isMainThread(){
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
	
	private Runnable autoDismissRunnable = this::dismiss;

}
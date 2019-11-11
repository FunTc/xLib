package com.tclibrary.xlib.view;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by FunTc on 2018/10/29.
 */
public class HttpProgressDialogHelper implements IProgressView {

	private static final class InstanceHolder {
		private static final HttpProgressDialogHelper INSTANCE = new HttpProgressDialogHelper();
	}

	public static HttpProgressDialogHelper instance() {
		return InstanceHolder.INSTANCE;
	}

	private IProgressView mIProgressView;
	private Handler mMainHandler;
	
	private HttpProgressDialogHelper() {
		mMainHandler = new Handler(Looper.getMainLooper());
		mIProgressView = new DefaultHttpProgressViewHolder();
	}
	
	public void init(IProgressView progressView){
		mIProgressView = progressView;
	}

	@Override
	public void show() {
		if (isMainThread()){
			mIProgressView.show();
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mIProgressView.show();
				}
			});
		}
	}

	@Override
	public void show(final CharSequence msg) {
		if (isMainThread()){
			mIProgressView.show(msg);
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mIProgressView.show(msg);
				}
			});
		}
	}

	@Override
	public boolean isShowing() {
		return mIProgressView.isShowing();
	}

	@Override
	public void dismiss() {
		if (isMainThread()){
			mIProgressView.dismiss();
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mIProgressView.dismiss();
				}
			});
		}
	}

	private boolean isMainThread(){
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

}
package com.tclibrary.xlib.http;

import com.tclibrary.xlib.view.HttpProgressDialogHelper;

/**
 * Created by FunTc on 2018/10/29.
 */
public abstract class ProgressResponseResultObserver<T> extends ResponseResultObserver<T> {

	private String message;
	
	public ProgressResponseResultObserver(){ }

	public ProgressResponseResultObserver(String msg){
		message = msg;
	}
	
	@Override
	protected void onStartRequest() {
		HttpProgressDialogHelper.instance().show(message);
	}

	@Override
	protected void onEndRequest() {
		HttpProgressDialogHelper.instance().dismiss();
	}
}

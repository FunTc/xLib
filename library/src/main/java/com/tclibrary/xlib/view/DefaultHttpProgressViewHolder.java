package com.tclibrary.xlib.view;

import android.content.Context;

import com.tclibrary.xlib.AppManager;

/**
 * Created by FunTc on 2018/10/29.
 */
public class DefaultHttpProgressViewHolder implements IProgressView {

	private ProgressDialog mDialog;
	
	
	@Override
	public void show() {
		show(null, null);
	}

	@Override
	public void show(Context context) {
		show(context, null);
	}

	@Override
	public void show(final CharSequence msg) {
		show(null, msg);
	}

	@Override
	public void show(Context context, CharSequence msg) {
		if (isShowing()) dismiss();
		if (context == null) {
			context = AppManager.instance().getTopActivity();
		}
		mDialog = new ProgressDialog(context);
		mDialog.setMessage(msg);
		mDialog.show();
	}

	@Override
	public boolean isShowing() {
		return mDialog != null && mDialog.isShowing();
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			mDialog.dismiss();
		}
		mDialog = null;
	}
	
}

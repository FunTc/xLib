package com.tclibrary.xlib.view;

import android.content.DialogInterface;

import com.tclibrary.xlib.AppManager;

/**
 * Created by FunTc on 2018/10/29.
 */
public class DefaultHttpProgressViewHolder implements IProgressView {

	private ProgressDialog mDialog;
	
	
	@Override
	public void show() {
		show(null);
	}

	@Override
	public void show(final CharSequence msg) {
		if (isShowing()) dismiss();
		mDialog = new ProgressDialog(AppManager.instance().getTopActivity());
		mDialog.setMessage(msg);
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mDialog = null;
			}
		});
		mDialog.show();
	}

	@Override
	public boolean isShowing() {
		return mDialog != null && mDialog.isShowing();
	}

	@Override
	public void dismiss() {
		if (mDialog != null){
			if (mDialog.isShowing()){
				mDialog.dismiss();
			}
			mDialog = null;
		}
	}
	
}

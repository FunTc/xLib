package com.tclibrary.xlib.view;

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
		mDialog.setOnDismissListener(dialog -> mDialog = null);
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

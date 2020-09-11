package com.tclibrary.xlib.view;

import android.content.Context;

/**
 * Created by FunTc on 2018/10/29.
 */
public interface IProgressView {
	
	void show();
	
	void show(Context context);
	
	void show(CharSequence msg);
	
	void show(Context context, CharSequence msg);
	
	boolean isShowing();
	
	void dismiss();
}

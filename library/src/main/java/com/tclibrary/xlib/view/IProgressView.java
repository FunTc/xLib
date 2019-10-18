package com.tclibrary.xlib.view;

/**
 * Created by TianCheng on 2018/10/29.
 */
public interface IProgressView {
	
	void show();
	
	void show(CharSequence msg);
	
	boolean isShowing();
	
	void dismiss();
}

package com.tclibrary.xlib.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by FunTc on 2018/11/5.
 */
public abstract class BaseActivityPlugin extends LifecyclePlugin {

	protected Activity mActivity;
	
	public void onAttachActivity(Activity activity){
		mActivity = activity;
	}
	
	public void onPostCreate(Bundle savedInstanceState){ }

	public void onSaveInstanceState(Bundle outState) { }

	public void onRestoreInstanceState(Bundle savedInstanceState) { }

	public void onActivityResult(int requestCode, int resultCode, Intent data) { }

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return false;
	}

	public boolean onBackPressed() {
		return false;
	}
	
}

package com.tclibrary.xlib.eventbus;

import android.support.annotation.NonNull;

/**
 * Created by TianCheng on 2018/8/31.
 */
public interface OnEventListener {
	
	void onEventResult(@NonNull Event event);
}

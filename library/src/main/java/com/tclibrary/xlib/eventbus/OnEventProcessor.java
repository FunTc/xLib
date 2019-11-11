package com.tclibrary.xlib.eventbus;

import android.support.annotation.NonNull;

/**
 * Created by FunTc on 2018/8/31.
 */
public interface OnEventProcessor {
	
	void onProcessEvent(@NonNull Event event) throws Exception;
}

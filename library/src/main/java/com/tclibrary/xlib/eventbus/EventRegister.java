package com.tclibrary.xlib.eventbus;

import android.support.annotation.NonNull;

/**
 * Created by TianCheng on 2018/9/10.
 */
public interface EventRegister {

	EventRegister setEventProcessor(@NonNull OnEventProcessor processor);
	EventRegister addEventListener(@NonNull OnEventListener listener);
	void register(Object object);
	EventPoster registerAt(Object object);
	
}

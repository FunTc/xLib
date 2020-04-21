package com.tclibrary.xlib.eventbus;

/**
 * Created by FunTc on 2018/9/10.
 */
public interface EventPoster {

	EventPoster setValues(Object... values);
	EventPoster delay(long delay);
	void post();
	void postTo(ThreadMode mode);
	
	
}

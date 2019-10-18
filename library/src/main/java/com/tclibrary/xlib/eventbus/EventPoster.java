package com.tclibrary.xlib.eventbus;

/**
 * Created by TianCheng on 2018/9/10.
 */
public interface EventPoster {

	EventPoster addParams(Object... params);
	EventPoster processOn(ThreadMode threadMode);
	EventPoster observeOn(ThreadMode threadMode);
	void post();
	void post(long delay);
	
}

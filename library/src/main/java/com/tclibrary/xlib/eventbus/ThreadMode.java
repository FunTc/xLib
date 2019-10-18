package com.tclibrary.xlib.eventbus;

/**
 * Created by TianCheng on 2018/9/17.
 */
public enum ThreadMode {
	
	/** 将事件执行在UI线程 */
	MAIN,
	
	/** 将事件执行在一个子线程中 */
	ASYNC
}

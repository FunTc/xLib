package com.tclibrary.xlib.plugin;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * Created by FunTc on 2018/11/5.
 */
public abstract class LifecyclePlugin implements IPlugin, LifecycleObserver {
	
	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE) protected void onCreate() { }
	
	@OnLifecycleEvent(Lifecycle.Event.ON_START) protected void onStart() { }

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME) protected void onResume() { }

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) protected void onPause() { }

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP) protected void onStop() { }

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) protected void onDestroy() { }
	
}

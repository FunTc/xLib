package com.tclibrary.xlib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by FunTc on 2018/10/19.
 */
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
	
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		AppManager.instance().addActivity(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {
		
	}

	@Override
	public void onActivityResumed(Activity activity) {
		AppManager.instance().setCurrentActivity(activity);
	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		if (AppManager.instance().getCurrentActivity() == activity) {
			AppManager.instance().setCurrentActivity(null);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		AppManager.instance().removeActivity(activity);
	}
}

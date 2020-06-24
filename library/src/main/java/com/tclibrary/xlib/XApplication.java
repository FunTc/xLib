package com.tclibrary.xlib;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * Created by FunTc on 2018/1/9.
 * 
 */

public class XApplication extends Application {

	private static Context 	applicationContext;

	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(new ActivityLifecycle());
		applicationContext = getApplicationContext();
		Utils.init(this);
		CrashHandler.getInstance().init(this);
	}

	public static Context getAppContext(){
		return applicationContext;
	}
	
}

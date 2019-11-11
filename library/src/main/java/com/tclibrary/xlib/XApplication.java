package com.tclibrary.xlib;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

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
		initLogger();
		Utils.init(this);
		CrashHandler.getInstance().init(this);
		
	}

	public static Context getAppContext(){
		return applicationContext;
	}
	
	private void initLogger(){
		String[] strings = getPackageName().split("[.]");
		String tag = strings[strings.length - 1] + "_log";
		FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				.showThreadInfo(false)
				.tag(tag)
				.build();
		Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
			@Override
			public boolean isLoggable(int priority, @Nullable String tag) {
				return isLoggerEnable();
			}
		});
	}
	
	protected boolean isLoggerEnable(){
		return BuildConfig.DEBUG;
	}
	
}

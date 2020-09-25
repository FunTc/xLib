package com.tclibrary.xlib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tclibrary.xlib.eventbus.EventBus;
import com.tclibrary.xlib.view.HttpProgressDialogHelper;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/10/19.
 */
public class AppActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    public static final String APP_IS_BACKGROUND	= "app_is_background";

    private int count;

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        AppManager.instance().addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        count++;
        if (count == 1) {
            EventBus.poster(APP_IS_BACKGROUND).setValues(false).post();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        AppManager.instance().setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        count--;
        if (count == 0) {
            EventBus.poster(APP_IS_BACKGROUND).setValues(true).post();
        }
        if (AppManager.instance().getCurrentActivity() == activity) {
            AppManager.instance().setCurrentActivity(null);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        HttpProgressDialogHelper.instance().dismiss();
        AppManager.instance().removeActivity(activity);
    }
}

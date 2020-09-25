package com.tclibrary.xlib.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by FunTc on 2018/11/5.
 */
public abstract class BaseActivityPlugin extends LifecycleObserverPlugin implements LifecycleOwner {

    protected AppCompatActivity mActivity;

    public void onAttachActivity(AppCompatActivity activity){
        mActivity = activity;
    }

    public void onPostCreate(Bundle savedInstanceState){ }

    public void onSaveInstanceState(Bundle outState) { }

    public void onRestoreInstanceState(Bundle savedInstanceState) { }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mActivity.getLifecycle();
    }
}

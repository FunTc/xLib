package com.tclibrary.xlib.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.tclibrary.xlib.plugin.BaseActivityPlugin;
import com.tclibrary.xlib.plugin.PluginHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by TianCheng on 2020/04/23.
 */
class ActivityPluginHandler {

    private PluginHelper mPluginHelper;
    
    void addPlugin(@NonNull BaseActivityPlugin plugin) {
        if (mPluginHelper == null)
            mPluginHelper = new PluginHelper();
        mPluginHelper.addPlugin(plugin);
    }

    void onPostCreate(@Nullable Bundle savedInstanceState) {
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                plugin.onPostCreate(savedInstanceState);
            }
        }
    }

    void onSaveInstanceState(@NonNull Bundle outState) {
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                plugin.onSaveInstanceState(outState);
            }
        }
    }

    void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                plugin.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                plugin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isHandled = false;
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                if (plugin.dispatchTouchEvent(ev)){
                    isHandled = true;
                }
            }
        }
        return isHandled;
    }
    
    boolean onBackPressed() {
        boolean isHandled = false;
        if (mPluginHelper != null){
            for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
                if (plugin.onBackPressed()){
                    isHandled = true;
                }
            }
        }
        return isHandled;
    }
    
    void clear() {
        if (mPluginHelper != null) {
            mPluginHelper.clear();
            mPluginHelper = null;
        }
    }
    
}

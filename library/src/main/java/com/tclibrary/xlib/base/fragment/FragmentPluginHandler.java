package com.tclibrary.xlib.base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tclibrary.xlib.plugin.BaseFragmentPlugin;
import com.tclibrary.xlib.plugin.PluginHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by FunTc on 2020/04/23.
 */
class FragmentPluginHandler {
    
    private PluginHelper mPluginHelper;

    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onViewCreated(view, savedInstanceState);
            }
        }
    }

    void addPlugin(@NonNull BaseFragmentPlugin plugin){
        if (mPluginHelper == null)
            mPluginHelper = new PluginHelper();
        mPluginHelper.addPlugin(plugin);
    }

    void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onActivityCreated(savedInstanceState);
            }
        }
    }

    void onDestroyView() {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onDestroyView();
            }
        }
    }

    void onDetach() {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onDetach();
            }
        }
    }

    void onSaveInstanceState(Bundle outState) {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onSaveInstanceState(outState);
            }
        }
    }

    void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onViewStateRestored(savedInstanceState);
            }
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
                plugin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    
    boolean onBackPressed() {
        boolean isHandled = false;
        if (mPluginHelper != null){
            for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
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

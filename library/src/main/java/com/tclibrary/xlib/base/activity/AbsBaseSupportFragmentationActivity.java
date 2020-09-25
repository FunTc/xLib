package com.tclibrary.xlib.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.tclibrary.xlib.base.DefaultToolbar;
import com.tclibrary.xlib.base.IToolbar;
import com.tclibrary.xlib.base.IToolbarHolder;
import com.tclibrary.xlib.base.ToolbarConfig;
import com.tclibrary.xlib.plugin.BaseActivityPlugin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by FunTc on 2018/10/8.
 */
public abstract class AbsBaseSupportFragmentationActivity extends SupportActivity implements IToolbarHolder {

    private ActivityPluginHandler mPluginHandler;
    protected IToolbar mIToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginHandler = new ActivityPluginHandler();
        preSetContentView();

        if (getLayoutView() != null) {
            setContentView(getLayoutView());
        } else {
            setContentView(getLayoutViewId());
        }

        mIToolbar = createToolbar();
    }

    @Override
    public void onToolbarConfig(@NonNull ToolbarConfig config) {}

    @Override
    public void onTitleRightBtnClick(@NonNull View v) {}

    @Override
    public void onCloseBtnClick(@NonNull View v) {}

    @NonNull
    @Override
    public IToolbar createToolbar() {
        return new DefaultToolbar(this);
    }

    protected int getLayoutViewId() {
        return 0;
    }

    protected View getLayoutView() {
        return null;
    }

    protected void preSetContentView() {}

    protected void addPlugin(@NonNull BaseActivityPlugin plugin){
        getLifecycle().addObserver(plugin);
        plugin.onAttachActivity(this);
        mPluginHandler.addPlugin(plugin);
    }

    @Override
    protected void onDestroy() {
        mPluginHandler.clear();
        mPluginHandler = null;
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPluginHandler.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPluginHandler.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPluginHandler.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPluginHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPluginHandler.dispatchTouchEvent(ev)){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressedSupport() {
        if (mPluginHandler.onBackPressed()){
            return;
        }
        super.onBackPressedSupport();
    }
}

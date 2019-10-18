package com.tclibrary.xlib.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.tclibrary.xlib.base.ToolbarConfig;
import com.tclibrary.xlib.plugin.BaseActivityPlugin;
import com.tclibrary.xlib.plugin.PluginHelper;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by TianCheng on 2018/10/8.
 */
public abstract class BaseSupportFragmentationActivity extends SupportActivity implements IBaseActivity{

	private final BaseActivityDelegate mDelegate = new BaseActivityDelegate(this);
	private PluginHelper mPluginHelper;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDelegate.onCreate();
	}

	@Override
	protected void onDestroy() {
		mDelegate.onDestroy();
		if (mPluginHelper != null) mPluginHelper.clear();
		super.onDestroy();
	}

	@Override
	public void onToolbarConfig(@NonNull ToolbarConfig config) {}

	@Override
	public void preSetContentView() {}

	@Override
	public void onTitleRightBtnClick(@NonNull View v) {}

	@Override
	public Toolbar getToolbar() {
		return mDelegate.getToolbar();
	}

	@Override
	public TextView getTitleTextView() {
		return mDelegate.getTitleTextView();
	}

	@Override
	public View getTitleRightBtn() {
		return mDelegate.getTitleRightBtn();
	}

	@Override
	public void onCloseBtnClick(@NonNull View v) {}

	protected void addPlugin(@NonNull BaseActivityPlugin plugin){
		if (mPluginHelper == null)
			mPluginHelper = new PluginHelper();
		getLifecycle().addObserver(plugin);
		mPluginHelper.addPlugin(plugin);
		plugin.onAttachActivity(this);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (mPluginHelper != null){
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				plugin.onPostCreate(savedInstanceState);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mPluginHelper != null){
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				plugin.onSaveInstanceState(outState);
			}
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mPluginHelper != null){
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				plugin.onRestoreInstanceState(savedInstanceState);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mPluginHelper != null){
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				plugin.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mPluginHelper != null){
			boolean isHandled = false;
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				if (plugin.dispatchTouchEvent(ev)){
					isHandled = true;
				}
			}
			if (!isHandled) return super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onBackPressedSupport() {
		if (mPluginHelper != null){
			boolean isHandled = false;
			for (BaseActivityPlugin plugin : mPluginHelper.getPlugins(BaseActivityPlugin.class)){
				if (plugin.onBackPressed()){
					isHandled = true;
				}
			}
			if (isHandled) {
				return;
			}
		}
		super.onBackPressedSupport();
	}
}

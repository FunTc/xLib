package com.tclibrary.xlib.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tclibrary.xlib.base.ToolbarConfig;
import com.tclibrary.xlib.plugin.BaseFragmentPlugin;
import com.tclibrary.xlib.plugin.PluginHelper;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by TianCheng on 2018/9/27.
 */
public abstract class BaseFragment extends SupportFragment implements IBaseFragment{
	
	private final BaseFragmentDelegate mBaseFragmentDelegate = new BaseFragmentDelegate(this);
	private PluginHelper mPluginHelper;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mBaseFragmentDelegate.onAttach();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(getLayoutId(), null);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBaseFragmentDelegate.onViewCreated();
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onViewCreated(view, savedInstanceState);
			}
		}
	}

	@Override
	public void onDestroy() {
		mBaseFragmentDelegate.onDestroy();
		if (mPluginHelper != null) mPluginHelper.clear();
		super.onDestroy();
	}

	@Override
	public void onToolbarConfig(@NonNull ToolbarConfig config) {}

	@Override
	public void onTitleRightBtnClick(@NonNull View v) {}

	@Override
	public Toolbar getToolbar() {
		return mBaseFragmentDelegate.getToolbar();
	}

	@Override
	public TextView getTitleTextView() {
		return mBaseFragmentDelegate.getTitleTextView();
	}

	@Override
	public View getTitleRightBtn() {
		return mBaseFragmentDelegate.getTitleRightBtn();
	}

	@Override
	public void onCloseBtnClick(@NonNull View v) {}

	protected void addPlugin(@NonNull BaseFragmentPlugin plugin){
		if (mPluginHelper == null)
			mPluginHelper = new PluginHelper();
		getLifecycle().addObserver(plugin);
		mPluginHelper.addPlugin(plugin);
		plugin.onAttachFragment(this);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onActivityCreated(savedInstanceState);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onDestroyView();
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onDetach();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onSaveInstanceState(outState);
			}
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onViewStateRestored(savedInstanceState);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mPluginHelper != null){
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				plugin.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	public boolean onBackPressedSupport() {
		if (mPluginHelper != null){
			boolean isHandled = false;
			for (BaseFragmentPlugin plugin : mPluginHelper.getPlugins(BaseFragmentPlugin.class)){
				if (plugin.onBackPressed()){
					isHandled = true;
				}
			}
			if (isHandled) {
				return true;
			}
		}
		return super.onBackPressedSupport();
	}

}

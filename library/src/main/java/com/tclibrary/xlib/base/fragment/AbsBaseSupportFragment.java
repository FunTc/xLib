package com.tclibrary.xlib.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tclibrary.xlib.base.DefaultToolbar;
import com.tclibrary.xlib.base.IToolbar;
import com.tclibrary.xlib.base.IToolbarHolder;
import com.tclibrary.xlib.base.ToolbarConfig;
import com.tclibrary.xlib.plugin.BaseFragmentPlugin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by FunTc on 2018/9/27.
 */
public abstract class AbsBaseSupportFragment extends SupportFragment implements IToolbarHolder {
	
	private FragmentPluginHandler mPluginHandler;
	protected IToolbar mIToolbar;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		mPluginHandler = new FragmentPluginHandler();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mIToolbar = createToolbar();
		mPluginHandler.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		mPluginHandler.clear();
		mPluginHandler = null;
		super.onDestroy();
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

	protected void addPlugin(@NonNull BaseFragmentPlugin plugin){
		getLifecycle().addObserver(plugin);
		plugin.onAttachFragment(this);
		mPluginHandler.addPlugin(plugin);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPluginHandler.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPluginHandler.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mPluginHandler.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mPluginHandler.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		mPluginHandler.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPluginHandler.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onBackPressedSupport() {
		if (mPluginHandler.onBackPressed()) {
			return true;
		}
		return super.onBackPressedSupport();
	}

}

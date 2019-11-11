package com.tclibrary.xlib.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by FunTc on 2018/11/5.
 */
public abstract class BaseFragmentPlugin extends LifecyclePlugin {
	
	protected Fragment mFragment;
	
	public void onAttachFragment(@NonNull Fragment fragment){
		mFragment = fragment;
	}

	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){}
	
	public void onActivityCreated(@Nullable Bundle savedInstanceState){}

	public void onDestroyView(){}

	public void onDetach(){}

	public void onSaveInstanceState(Bundle outState) { }

	public void onViewStateRestored(Bundle savedInstanceState) { }

	public void onActivityResult(int requestCode, int resultCode, Intent data) { }

	public boolean onBackPressed() {
		return false;
	}
	
}

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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by FunTc on 2020/04/23.
 */
public abstract class AbsBaseFragment extends Fragment implements IToolbarHolder {

    private FragmentPluginHandler mPluginHandler;
    protected IToolbar mIToolbar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mPluginHandler = new FragmentPluginHandler();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
    public void onStart() {
        super.onStart();
        mBackPressedCallback.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBackPressedCallback.setEnabled(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mBackPressedCallback.setEnabled(!hidden);
    }

    private OnBackPressedCallback mBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (!mPluginHandler.onBackPressed()) { //没有消费返回事件，就把事件继续传递下去
                setEnabled(false);
                requireActivity().onBackPressed();
                setEnabled(true);
            }
        }
    };
    
}

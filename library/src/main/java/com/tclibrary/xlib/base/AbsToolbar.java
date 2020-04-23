package com.tclibrary.xlib.base;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tclibrary.xlib.R;

import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Created by FunTc on 2020/04/23.
 */
public abstract class AbsToolbar implements IToolbar, View.OnClickListener {

    protected IToolbarHolder mToolbarHolder;
    protected Toolbar mToolbar;
    protected TextView mTvTitle;
    protected View mBtnTitleRight;
    
    public AbsToolbar(IToolbarHolder holder) {
        ToolbarConfig config = new ToolbarConfig();
        holder.onToolbarConfig(config);
        if (config.hasTitle) {
            mToolbarHolder = holder;
            initToolbar(config);
        }
    }
    
    private void initToolbar(@NonNull ToolbarConfig config) {
        Activity activity = null;
        if (mToolbarHolder instanceof Activity) {
            activity = (Activity) mToolbarHolder;
            mToolbar = activity.findViewById(R.id.toolbar);
        } else if (mToolbarHolder instanceof Fragment) {
            activity = ((Fragment) mToolbarHolder).requireActivity();
            View fragmentRootView = ((Fragment) mToolbarHolder).getView();
            if (fragmentRootView == null) throw new RuntimeException("To use the title, the fragment must have a layout");
            mToolbar = fragmentRootView.findViewById(R.id.toolbar);
        }
        if (activity == null) return;
        if (mToolbar == null) throw new NoSuchElementException("Your layout need include toolbar");
        configToolbar(config, activity);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnTitleRight) {
            mToolbarHolder.onTitleRightBtnClick(v);
        }
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public TextView getTitleTextView() {
        return mTvTitle;
    }

    @Override
    public View getTitleRightBtn() {
        return mBtnTitleRight;
    }

    protected abstract void configToolbar(@NonNull ToolbarConfig config, @NonNull Activity hostActivity);
}

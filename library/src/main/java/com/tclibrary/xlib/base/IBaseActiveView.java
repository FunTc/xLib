package com.tclibrary.xlib.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by FunTc on 2018/10/18.
 */
public interface IBaseActiveView {

	int getLayoutId();

	void onToolbarConfig(@NonNull ToolbarConfig config);

	void onTitleRightBtnClick(@NonNull View v);

	Toolbar getToolbar();

	TextView getTitleTextView();

	View getTitleRightBtn();
	
	void onCloseBtnClick(@NonNull View v);
}

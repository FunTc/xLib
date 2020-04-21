package com.tclibrary.xlib.base;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

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

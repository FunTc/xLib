package com.tclibrary.xlib.base;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2020/04/23.
 */
public interface IToolbarHolder {

    void onToolbarConfig(@NonNull ToolbarConfig config);

    void onTitleRightBtnClick(@NonNull View v);

    void onCloseBtnClick(@NonNull View v);

    @NonNull IToolbar createToolbar();
    
}

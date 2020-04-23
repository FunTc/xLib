package com.tclibrary.xlib.base;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

/**
 * Created by FunTc on 2020/04/23.
 */
public interface IToolbar {
    
    Toolbar getToolbar();

    TextView getTitleTextView();

    View getTitleRightBtn();
}

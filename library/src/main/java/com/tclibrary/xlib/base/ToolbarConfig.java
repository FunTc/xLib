package com.tclibrary.xlib.base;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Created by FunTc on 2018/9/28.
 */
public class ToolbarConfig {

	public boolean 		hasTitle = true;
	public boolean 		hasBackButton;
	@DrawableRes 
	public int 			mBackBtnRes;
	public CharSequence mTitle;
	public CharSequence mRightBtnText;
	@ColorInt
	public int			mRightBtnTextColor = -1;
	public Drawable 	mRightBtnImage;
	@DrawableRes 
	public int 			mRightBtnImageRes;
	public int			mElevation = -1;
	public boolean		hasCloseButton;
	@DrawableRes 
	public int			mCloseBtnRes;
	@ColorInt
	public int			mTitleTextColor = -1;
}

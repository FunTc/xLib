package com.tclibrary.xlib.base;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

/**
 * Created by FunTc on 2018/9/28.
 */
public class ToolbarConfig {

	public boolean 		hasTitle;
	public boolean 		hasBackButton;
	@DrawableRes 
	public int 			backBtnRes;
	public CharSequence title;
	public CharSequence rightBtnText;
	@ColorInt
	public int 			rightBtnTextColor = -1;
	public Drawable 	rightBtnImage;
	@DrawableRes 
	public int 			rightBtnImageRes;
	public int 			elevation = -1;
	public boolean		hasCloseButton;
	@DrawableRes 
	public int 			closeBtnRes;
	@ColorInt
	public int 			titleTextColor = -1;
}

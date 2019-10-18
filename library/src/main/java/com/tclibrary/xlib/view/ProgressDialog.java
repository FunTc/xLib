package com.tclibrary.xlib.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tclibrary.xlib.R;

/**
 * Created by TianCheng on 2018/10/29.
 */
public class ProgressDialog extends Dialog {

	private TextView mTvMsg;
	
	public ProgressDialog(@NonNull Context context) {
		super(context);
		setContentView(R.layout.view_progress);
		mTvMsg = findViewById(R.id.tv);
		Window window = getWindow();
		if (window != null){
			WindowManager.LayoutParams params = window.getAttributes();
			params.dimAmount = 0;
			window.setAttributes(params);
		}
		setCanceledOnTouchOutside(false);
	}
	
	public void setMessage(CharSequence msg){
		if (!TextUtils.isEmpty(msg)){
			mTvMsg.setText(msg);
			mTvMsg.setVisibility(View.VISIBLE);
		}
	}
	
}

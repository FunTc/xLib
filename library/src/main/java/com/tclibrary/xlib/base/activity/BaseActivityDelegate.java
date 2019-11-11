package com.tclibrary.xlib.base.activity;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.tclibrary.xlib.R;
import com.tclibrary.xlib.base.ToolbarConfig;

/**
 * Created by FunTc on 2018/9/28.
 */
class BaseActivityDelegate {
	
	private AppCompatActivity mActivity;
	private IBaseActivity mBaseActivity;
	
	private ToolbarConfig mToolbarConfig;
	private Toolbar mToolbar;
	private TextView mTvTitle;
	private View mBtnTitleRight;

	BaseActivityDelegate(IBaseActivity baseActivity){
		mActivity = (AppCompatActivity) baseActivity;
		mBaseActivity = baseActivity;
	}
	
	void onCreate(){
		mBaseActivity.preSetContentView();
		mActivity.setContentView(mBaseActivity.getLayoutId());
		mBaseActivity.onToolbarConfig(mToolbarConfig = new ToolbarConfig());
		initToolbar();
	}

	void onDestroy(){
		mActivity = null;
		mBaseActivity = null;
		mToolbarConfig = null;
		mToolbar = null;
		mTvTitle = null;
		mBtnTitleRight = null;
	}
	
	private void initToolbar(){
		if (mToolbarConfig.hasTitle){
			mToolbar = mActivity.findViewById(R.id.toolbar);
			if (mToolbarConfig.hasBackButton){
				if (mToolbarConfig.mBackBtnRes != 0){
					mToolbar.setNavigationIcon(mToolbarConfig.mBackBtnRes);
				} else {
					mToolbar.setNavigationIcon(R.drawable.icon_back);
				}
				mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mActivity.onBackPressed();
					}
				});
			}
			mTvTitle = mToolbar.findViewById(R.id.tv_title);
			if (!TextUtils.isEmpty(mToolbarConfig.mTitle)){
				mTvTitle.setText(mToolbarConfig.mTitle);
			}
			if (mToolbarConfig.mTitleTextColor != -1){
				mTvTitle.setTextColor(mToolbarConfig.mTitleTextColor);
			}
			if (!TextUtils.isEmpty(mToolbarConfig.mRightBtnText)){
				mBtnTitleRight = mToolbar.findViewById(R.id.tv_btn_right);
				((TextView)mBtnTitleRight).setText(mToolbarConfig.mRightBtnText);
				if (mToolbarConfig.mRightBtnTextColor != -1){
					((TextView)mBtnTitleRight).setTextColor(mToolbarConfig.mRightBtnTextColor);
				}
				mBtnTitleRight.setVisibility(View.VISIBLE);
				mBtnTitleRight.setOnClickListener(mTitleRightClickListener);
			} else if (mToolbarConfig.mRightBtnImage != null || mToolbarConfig.mRightBtnImageRes != 0){
				mBtnTitleRight = mToolbar.findViewById(R.id.iv_btn_right);
				((ImageView)mBtnTitleRight).setImageDrawable(mToolbarConfig.mRightBtnImageRes != 0 ?
						ContextCompat.getDrawable(mActivity, mToolbarConfig.mRightBtnImageRes) : mToolbarConfig.mRightBtnImage);
				mBtnTitleRight.setVisibility(View.VISIBLE);
				mBtnTitleRight.setOnClickListener(mTitleRightClickListener);
			}
			ViewCompat.setElevation(mToolbar, mToolbarConfig.mElevation == -1 ? SizeUtils.dp2px(3) : mToolbarConfig.mElevation);

			if (mToolbarConfig.hasCloseButton){
				if (mToolbarConfig.hasBackButton){
					View divLine = new View(mActivity);
					Toolbar.LayoutParams lp = new Toolbar.LayoutParams(SizeUtils.dp2px(1), Toolbar.LayoutParams.MATCH_PARENT);
					lp.topMargin = lp.bottomMargin = SizeUtils.dp2px(12);
					divLine.setLayoutParams(lp);
					divLine.setBackgroundColor(Color.parseColor("#75757575"));
					mToolbar.addView(divLine);
				}
				AppCompatImageView closeBtn = new AppCompatImageView(mActivity, null, R.attr.toolbarNavigationButtonStyle);
				Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
				closeBtn.setLayoutParams(lp);
				closeBtn.setImageResource(mToolbarConfig.mCloseBtnRes == 0 ? R.drawable.icon_close : mToolbarConfig.mCloseBtnRes);
				mToolbar.addView(closeBtn);
				closeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mBaseActivity.onCloseBtnClick(v);
					}
				});
			}
			
		}
	}

	private View.OnClickListener mTitleRightClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mBaseActivity.onTitleRightBtnClick(v);
		}
	};
	
	Toolbar getToolbar(){
		return mToolbar;
	}

	TextView getTitleTextView(){
		return mTvTitle;
	}

	View getTitleRightBtn(){
		return mBtnTitleRight;
	}
	
}

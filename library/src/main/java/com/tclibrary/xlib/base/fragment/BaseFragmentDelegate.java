package com.tclibrary.xlib.base.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.tclibrary.xlib.R;
import com.tclibrary.xlib.base.ToolbarConfig;

import java.util.NoSuchElementException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by FunTc on 2018/9/29.
 */
class BaseFragmentDelegate {

	private SupportFragment mFragment;
	private IBaseFragment mBaseFragment;

	private ToolbarConfig mToolbarConfig;
	private Toolbar mToolbar;
	private TextView mTvTitle;
	private View mBtnTitleRight;

	BaseFragmentDelegate(IBaseFragment baseFragment){
		if (baseFragment instanceof SupportFragment){
			mFragment = (SupportFragment) baseFragment;
		} else {
			throw new RuntimeException("your fragment must extend \"SupportFragment\"");
		}
		mBaseFragment = baseFragment;
	}
	
	void onAttach(){
		if (!(mFragment.getActivity() instanceof AppCompatActivity)){
			throw new RuntimeException("Fragment must use in AppCompatActivity");
		}
	}

	void onViewCreated(){
		mBaseFragment.onToolbarConfig(mToolbarConfig = new ToolbarConfig());
		initToolbar();
	}

	void onDestroy(){
		mFragment = null;
		mBaseFragment = null;
		mToolbarConfig = null;
		mToolbar = null;
		mTvTitle = null;
		mBtnTitleRight = null;
	}

	private void initToolbar(){
		View fragmentRootView = mFragment.getView();
		if (fragmentRootView == null)
			throw new RuntimeException("Your fragment has no layout");
		if (mToolbarConfig.hasTitle){
			mToolbar = fragmentRootView.findViewById(R.id.toolbar);
			if (mToolbar == null)
				throw new NoSuchElementException("Your layout need include toolbar");
			final Activity activity = mFragment.getActivity();
			if (activity == null)
				throw new RuntimeException("your fragment not attach activity");
			if (mToolbarConfig.hasBackButton){
				if (mToolbarConfig.mBackBtnRes != 0){
					mToolbar.setNavigationIcon(mToolbarConfig.mBackBtnRes);
				} else {
					mToolbar.setNavigationIcon(R.drawable.icon_back);
				}
				mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						activity.onBackPressed();
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
						ContextCompat.getDrawable(activity, mToolbarConfig.mRightBtnImageRes) : mToolbarConfig.mRightBtnImage);
				mBtnTitleRight.setVisibility(View.VISIBLE);
				mBtnTitleRight.setOnClickListener(mTitleRightClickListener);
			}
			ViewCompat.setElevation(mToolbar, mToolbarConfig.mElevation == -1 ? SizeUtils.dp2px(3) : mToolbarConfig.mElevation);

			if (mToolbarConfig.hasCloseButton){
				if (mToolbarConfig.hasBackButton){
					View divLine = new View(activity);
					Toolbar.LayoutParams lp = new Toolbar.LayoutParams(SizeUtils.dp2px(1), Toolbar.LayoutParams.MATCH_PARENT);
					lp.topMargin = lp.bottomMargin = SizeUtils.dp2px(12);
					divLine.setLayoutParams(lp);	
					divLine.setBackgroundColor(Color.parseColor("#75757575"));
					mToolbar.addView(divLine);
				}
				AppCompatImageView closeBtn = new AppCompatImageView(activity, null, R.attr.toolbarNavigationButtonStyle);
				Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
				closeBtn.setLayoutParams(lp);
				closeBtn.setImageResource(mToolbarConfig.mCloseBtnRes == 0 ? R.drawable.icon_close : mToolbarConfig.mCloseBtnRes);
				mToolbar.addView(closeBtn);
				closeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mBaseFragment.onCloseBtnClick(v);
					}
				});
			}
		}
	}

	private View.OnClickListener mTitleRightClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mBaseFragment.onTitleRightBtnClick(v);
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

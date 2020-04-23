package com.tclibrary.xlib.base;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.tclibrary.xlib.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * Created by FunTc on 2020/04/23.
 */
public class DefaultToolbar extends AbsToolbar {
    
    public DefaultToolbar(IToolbarHolder toolbarHolder) {
        super(toolbarHolder);
    }
    
    @Override
    protected void configToolbar(@NonNull ToolbarConfig config, @NonNull Activity hostActivity) {
        if (config.hasBackButton){
            if (config.backBtnRes != 0){
                mToolbar.setNavigationIcon(config.backBtnRes);
            } else {
                mToolbar.setNavigationIcon(R.drawable.icon_back);
            }
            mToolbar.setNavigationOnClickListener(v -> hostActivity.onBackPressed());
        }

        mTvTitle = mToolbar.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(config.title)){
            mTvTitle.setText(config.title);
        }
        if (config.titleTextColor != -1){
            mTvTitle.setTextColor(config.titleTextColor);
        }

        if (!TextUtils.isEmpty(config.rightBtnText)){
            mBtnTitleRight = mToolbar.findViewById(R.id.tv_btn_right);
            ((TextView)mBtnTitleRight).setText(config.rightBtnText);
            if (config.rightBtnTextColor != -1){
                ((TextView)mBtnTitleRight).setTextColor(config.rightBtnTextColor);
            }
            mBtnTitleRight.setVisibility(View.VISIBLE);
            mBtnTitleRight.setOnClickListener(this);
        } else if (config.rightBtnImage != null || config.rightBtnImageRes != 0){
            mBtnTitleRight = mToolbar.findViewById(R.id.iv_btn_right);
            ((ImageView)mBtnTitleRight).setImageDrawable(config.rightBtnImageRes != 0 ?
                    ContextCompat.getDrawable(hostActivity, config.rightBtnImageRes) : config.rightBtnImage);
            mBtnTitleRight.setVisibility(View.VISIBLE);
            mBtnTitleRight.setOnClickListener(this);
        }
        ViewCompat.setElevation(mToolbar, config.elevation == -1 ? SizeUtils.dp2px(3) : config.elevation);

        if (config.hasCloseButton){
            if (config.hasBackButton){
                View divLine = new View(hostActivity);
                Toolbar.LayoutParams lp = new Toolbar.LayoutParams(SizeUtils.dp2px(1), Toolbar.LayoutParams.MATCH_PARENT);
                lp.topMargin = lp.bottomMargin = SizeUtils.dp2px(12);
                divLine.setLayoutParams(lp);
                divLine.setBackgroundColor(Color.parseColor("#75757575"));
                mToolbar.addView(divLine);
            }
            AppCompatImageView closeBtn = new AppCompatImageView(hostActivity, null, R.attr.toolbarNavigationButtonStyle);
            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
            closeBtn.setLayoutParams(lp);
            closeBtn.setImageResource(config.closeBtnRes == 0 ? R.drawable.icon_close : config.closeBtnRes);
            mToolbar.addView(closeBtn);
            closeBtn.setOnClickListener(v -> mToolbarHolder.onCloseBtnClick(v));
        }
    }
}

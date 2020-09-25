package com.tclibrary.xlib.eventbus;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/8/31.
 */
public interface OnEventListener {

    void onEventResult(@NonNull Event event);
}

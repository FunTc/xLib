package com.tclibrary.xlib.eventbus;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/8/31.
 */
public interface EventProcessor {

    void onProcess(@NonNull Event event) throws Exception;

    default boolean isAsync() {
        return true;
    }

    void cancel(boolean mayInterruptIfRunning);
}

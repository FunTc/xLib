package com.tclibrary.xlib.eventbus;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/9/10.
 */
public interface EventRegister {

    EventRegister setEventProcessor(@NonNull EventProcessor processor);
    EventRegister addEventListener(@NonNull OnEventListener listener);
    EventRegister addEventListener(@NonNull OnEventListener listener, boolean notifyInActive);
    void register(Object object);
    EventPoster registerAt(Object object);

}

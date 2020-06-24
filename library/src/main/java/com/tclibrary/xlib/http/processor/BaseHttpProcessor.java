package com.tclibrary.xlib.http.processor;

import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.eventbus.EventProcessor;
import com.tclibrary.xlib.view.HttpProgressDialogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.util.Pair;

/**
 * Created by FunTc on 2020/06/24.
 */
public abstract class BaseHttpProcessor implements EventProcessor {
    
    
    @Override
    public final void onProcess(@NonNull Event event) throws Exception {
        Progress progress = event.findValue(Progress.class);
        showProgress(progress);
        try {
            onProcessInternal(event);
        } finally {
            hideProgress(progress);
        }
    }
    
    protected void showProgress(Progress progress) {
        boolean isShow = progress == null || progress.isShow();
        String msg = progress == null ? null : progress.getMessage();
        if (isShow) HttpProgressDialogHelper.instance().show(msg);
    }
    
    protected void hideProgress(Progress progress) {
        boolean isShow = progress == null || progress.isShow();
        if (isShow) HttpProgressDialogHelper.instance().dismiss();
    }
    
    
    protected abstract void onProcessInternal(@NonNull Event event) throws Exception;


    @SuppressWarnings("unchecked")
    protected static Map<String, Object> getMapFromParams(Object[] params){
        for (Object o : params){
            if (o instanceof Map){
                return (Map<String, Object>) o;
            }
        }
        return new ArrayMap<>();
    }

    @SuppressWarnings("unchecked")
    protected static List<Pair<String, Object>> getPairsFromParams(Object[] params){
        List<Pair<String, Object>> list = new ArrayList<>();
        for (Object o : params){
            if (o instanceof Pair){
                list.add((Pair<String, Object>) o);
            }
        }
        return list;
    }

}

package com.tclibrary.xlib.http.fileload;

/**
 * Created by FunTc on 2020/06/22.
 */
public interface FileLoadProgressListener {
    
    void onProgress(long current, long total, boolean isDone);
    
}

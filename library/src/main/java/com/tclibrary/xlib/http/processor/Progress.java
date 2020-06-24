package com.tclibrary.xlib.http.processor;

/**
 * Created by FunTc on 2020/06/24.
 */
public class Progress {
    
    public static final Progress NONE  = new Progress(false);
    
    public static Progress show(String message) {
        return new Progress(message);
    }
    
    
    private String message;
    private boolean isShow;
    
    private Progress(String message) {
        this.message = message;
        this.isShow = true;
    }
    
    private Progress(boolean isShow) {
        this.isShow = isShow;
    }

    public String getMessage() {
        return message;
    }

    public boolean isShow() {
        return isShow;
    }

}

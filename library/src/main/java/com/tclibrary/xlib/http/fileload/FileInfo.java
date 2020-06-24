package com.tclibrary.xlib.http.fileload;

/**
 * Created by FunTc on 2020/06/23.
 */
public class FileInfo {
    
    private String name;
    private String path;
    private String type;
    private long size;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}

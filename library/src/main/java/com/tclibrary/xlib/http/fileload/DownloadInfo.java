package com.tclibrary.xlib.http.fileload;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2020/06/23.
 */
public class DownloadInfo {
    
    private String path;
    private String fileType;
    private long totalSize;
    private long currentSize;
    private boolean isDone;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @NonNull
    @Override
    public String toString() {
        return "DownloadInfo{" +
                "path='" + path + '\'' +
                ", fileType='" + fileType + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", isDone=" + isDone +
                '}';
    }
}

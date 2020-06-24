package com.tclibrary.xlib.http.fileload;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by FunTc on 2020/06/22.
 */
public class FileResponseBody extends ResponseBody {

    private static final int MIN_INTERVAL = 50;
    
    private final ResponseBody mResponseBody;
    private final FileLoadProgressListener mListener;
    private BufferedSource bufferedSource;

    private long contentLength; //ResponseBody 内容长度，部分接口拿不到，会返回-1，此时会没有进度回调
    
    public FileResponseBody(@NonNull Response response, FileLoadProgressListener listener) {
        mResponseBody = response.body();
        mListener = listener;
        contentLength = mResponseBody.contentLength();
        if (contentLength == -1) { 
            contentLength = getContentLengthByHeader(response); 
        }
    }
    
    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @NonNull
    @Override
    public BufferedSource source() {
        if (mListener == null) {
            return mResponseBody.source();
        }
        if (bufferedSource == null) {
            Source source = source(mResponseBody);
            bufferedSource = Okio.buffer(source);
        }
        return bufferedSource;
    }


    private Source source(ResponseBody body) {
        
        return new ForwardingSource(body.source()) {
            //当前读取字节数
            long totalBytesRead = 0L;
            long lastTotalBytesRead = 0L;
            long lastTime;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                if (bytesRead == -1) {   //-1 代表读取完毕
                    if (contentLength == -1) contentLength = totalBytesRead;
                } else {
                    totalBytesRead += bytesRead; //未读取完，则累加已读取的字节
                }

                if (totalBytesRead > lastTotalBytesRead) {  //读取的字节数大于上次的，则更新进度
                    if (bytesRead != -1) {
                        long currentTime = System.currentTimeMillis();
                        //两次回调时间小于 MIN_INTERVAL 毫秒，直接返回，避免更新太频繁
                        if (currentTime - lastTime < MIN_INTERVAL) return bytesRead;
                        lastTime = currentTime;
                    }
                    lastTotalBytesRead = totalBytesRead;
                    mListener.onProgress(totalBytesRead, contentLength, bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }

    //从响应头 Content-Range 中，取 contentLength
    private long getContentLengthByHeader(Response response) {
        String headerValue = response.header("Content-Range");
        long contentLength = -1;
        if (headerValue != null) {
            //响应头Content-Range格式 : bytes 100001-20000000/20000001
            try {
                int divideIndex = headerValue.indexOf("/"); //斜杠下标
                int blankIndex = headerValue.indexOf(" ");
                String fromToValue = headerValue.substring(blankIndex + 1, divideIndex);
                String[] split = fromToValue.split("-");
                long start = Long.parseLong(split[0]); //开始下载位置
                long end = Long.parseLong(split[1]);   //结束下载位置
                contentLength = end - start + 1;       //要下载的总长度
            } catch (Exception ignore) {
            }
        }
        return contentLength;
    }
    
}

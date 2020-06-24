package com.tclibrary.xlib.http.fileload;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by FunTc on 2020/06/22.
 */
public class FileRequestBody extends RequestBody {

    public static final int MIN_INTERVAL = 50;
    
    private final RequestBody mRequestBody;
    private final FileLoadProgressListener mListener;
    private BufferedSink bufferedSink;
    
    public FileRequestBody(@NonNull RequestBody requestBody, FileLoadProgressListener listener) {
        mRequestBody = requestBody;
        mListener = listener;
    }
    
    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        //此行代码为兼容添加 HttpLoggingInterceptor 拦截器后，上传进度超过100%，达到200%问题
        if (sink instanceof Buffer) return;
        if (mListener == null) {
            mRequestBody.writeTo(sink);
            return;
        }
        
        if (bufferedSink == null) {
            Sink sk = sink(sink);
            bufferedSink = Okio.buffer(sk);
        }
        mRequestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;
            
            long lastBytesWritten = 0L;
            long lastTime;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if (bytesWritten <= lastBytesWritten) return;
                if (bytesWritten < contentLength) {
                    long currentTime = System.currentTimeMillis();
                    //两次回调时间间隔小于 MIN_INTERVAL 毫秒,直接返回,避免更新太频繁
                    if (currentTime - lastTime < MIN_INTERVAL) return;
                    lastTime = currentTime;
                }
                lastBytesWritten = bytesWritten;
                mListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }
    
}

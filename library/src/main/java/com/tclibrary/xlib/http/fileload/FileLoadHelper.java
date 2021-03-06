package com.tclibrary.xlib.http.fileload;

import android.net.Uri;
import android.text.TextUtils;

import com.tclibrary.xlib.http.HttpManager;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by FunTc on 2020/06/22.
 */
public final class FileLoadHelper {
    
    private FileLoadHelper() { }
    
    public static String getFileMimeType(String filePath) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(filePath);
        if (TextUtils.isEmpty(type)) {
            return "application/octet-stream"; //通用型
        }
        return type;
    }
    
    public static <T> Call uploadFile(@NonNull String url, @NonNull String filePath, FileLoadCallback<T> callback) {
        return uploadFile(url, new File(filePath), callback);
    }

    public static <T> Call uploadFile(@NonNull String url, @NonNull File file, FileLoadCallback<T> callback) {
        RequestBody fileBody = RequestBody.create(MediaType.parse(getFileMimeType(file.getPath())), file);
        FileRequestBody progressBody = new FileRequestBody(fileBody, callback);
        Request request = new Request.Builder()
                .url(url)
                .post(progressBody)
                .build();
        OkHttpClient client = HttpManager.instance().getOkHttpClient()
                .newBuilder()
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static <T> Call uploadFile(@NonNull String url, @NonNull Map<String, Object> params, FileLoadCallback<T> callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object obj = entry.getValue();
            if (obj instanceof File) {
                File file = (File) obj;
                builder.addFormDataPart(entry.getKey(), file.getName(), 
                        RequestBody.create(MediaType.parse(getFileMimeType(file.getPath())), file));
            } else {
                builder.addFormDataPart(entry.getKey(), obj == null ? "" : obj.toString());
            }
        }
        FileRequestBody progressBody = new FileRequestBody(builder.build(), callback);

        Request request = new Request.Builder()
                .url(url)
                .post(progressBody)
                .build();
        OkHttpClient client = HttpManager.instance().getOkHttpClient()
                .newBuilder()
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    
    public static Call downloadFile(@NonNull String url, @NonNull String destDir, FileLoadCallback<FileInfo> callback) {
        Uri uri = Uri.parse(url);
        String destFileName = uri.getLastPathSegment();
        if (TextUtils.isEmpty(destFileName)) {
            destFileName = "downloadFile";
        }
        return downloadFile(url, destDir, destFileName, callback);
    }
    
    public static Call downloadFile(@NonNull String url, @NonNull String destDir, @NonNull String destFileName, FileLoadCallback<FileInfo> callback) {
        File destFileDir = new File(destDir);
        if (!destFileDir.exists()) destFileDir.mkdirs();
        File destFile = new File(destFileDir, destFileName);

        OkHttpClient client = HttpManager.instance().getOkHttpClient()
                .newBuilder()
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null) callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (callback != null) callback.onFailure(call, new IOException("download failed"));
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) return;
                
                long contentLen = body.contentLength();
                MediaType mediaType = body.contentType();
                String contentType = mediaType == null ? "" : mediaType.toString();
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(destFileName);
                fileInfo.setPath(destFile.getPath());
                fileInfo.setSize(contentLen);
                fileInfo.setType(contentType);

                BufferedSink sink = Okio.buffer(Okio.sink(destFile));
                Buffer buffer = sink.buffer();
                long total = 0;
                long len;
                int bufferSize = 200 * 1024;
                BufferedSource source = body.source();
                long lastTotalBytesRead = 0;
                long lastTime = 0;
                while ((len = source.read(buffer, bufferSize)) != -1) {
                    sink.emit();
                    total += len;
                    long currentTime = System.currentTimeMillis();
                    if (callback != null && total > lastTotalBytesRead && currentTime - lastTime > 30) {
                        callback.onProgress(total, contentLen, false);
                        lastTotalBytesRead = total;
                        lastTime = currentTime;
                    }
                }
                source.close();
                sink.close();
                if (callback != null) {
                    callback.onProgress(contentLen, contentLen, true);
                    callback.onSuccess(call, response, fileInfo);
                }
            }
        });
        return call;
    }
    
    public static RxDownload rxDownload() {
        return new RxDownload();
    }
    
    public static class RxDownload {
        private String url;
        private String destDir;
        private String destFileName;
        
        RxDownload() { }

        public RxDownload url(@NonNull String url) {
            this.url = url;
            return this;
        }
        
        public RxDownload destination(@NonNull String destDir) {
            this.destDir = destDir;
            return this;
        }
        
        public RxDownload destination(@NonNull String destDir, String destFileName) {
            this.destDir = destDir;
            this.destFileName = destFileName;
            return this;
        }

        public Flowable<DownloadInfo> create() {
            if (TextUtils.isEmpty(destFileName)) {
                Uri uri = Uri.parse(url);
                destFileName = uri.getLastPathSegment();
                if (TextUtils.isEmpty(destFileName)) {
                    destFileName = "downloadFile";
                }
            }
            File destFileDir = new File(destDir);
            if (!destFileDir.exists()) destFileDir.mkdirs();
            final File destFile = new File(destFileDir, destFileName);
            
            return Flowable.create(new FlowableOnSubscribe<DownloadInfo>() {
                @Override
                public void subscribe(FlowableEmitter<DownloadInfo> emitter) throws Exception {
                    download(url, destFile, emitter);
                }
            }, BackpressureStrategy.BUFFER);
        }
        
        private void download(String url, File destFile, FlowableEmitter<DownloadInfo> emitter) {
            OkHttpClient client = HttpManager.instance().getOkHttpClient()
                    .newBuilder()
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = client.newCall(request);
            emitter.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    call.cancel();
                }

                @Override
                public boolean isDisposed() {
                    return call.isCanceled();
                }
            });
            
            try {
                Response response = call.execute();
                if (!response.isSuccessful()) {
                    throw new IOException("download failed");
                }
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("download failed");
                }
                long contentLen = responseBody.contentLength();
                MediaType mediaType = responseBody.contentType();
                String contentType = mediaType == null ? "" : mediaType.toString();
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setPath(destFile.getPath());
                downloadInfo.setTotalSize(contentLen);
                downloadInfo.setFileType(contentType);

                BufferedSink sink = Okio.buffer(Okio.sink(destFile));
                Buffer buffer = sink.buffer();
                long total = 0;
                long len;
                int bufferSize = 200 * 1024;
                BufferedSource source = responseBody.source();
                long lastTotalBytesRead = 0;
                long lastTime = 0;
                while ((len = source.read(buffer, bufferSize)) != -1) {
                    sink.emit();
                    total += len;
                    long currentTime = System.currentTimeMillis();
                    if (total > lastTotalBytesRead && currentTime - lastTime > 30) {
                        downloadInfo.setCurrentSize(total);
                        emitter.onNext(downloadInfo);
                        lastTotalBytesRead = total;
                        lastTime = currentTime;
                    }
                }
                source.close();
                sink.close();
                downloadInfo.setCurrentSize(contentLen);
                downloadInfo.setDone(true);
                emitter.onNext(downloadInfo);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }
    }
    
}

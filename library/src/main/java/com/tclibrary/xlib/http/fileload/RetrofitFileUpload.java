package com.tclibrary.xlib.http.fileload;

import com.tclibrary.xlib.eventbus.EventBus;
import com.tclibrary.xlib.eventbus.EventPoster;
import com.tclibrary.xlib.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by FunTc on 2020/06/23.
 */
public class RetrofitFileUpload {

    /**
     * Retrofit 上传文件进度使用EventBus的方式回调，此为EventTag
     */
    public static final String PROGRESS     = "retrofit_upload_progress";

    
    public static class FileRequestBodyConverter implements Converter<Map<String, Object>, RequestBody> {

        @Nullable
        @Override
        public RequestBody convert(@NonNull Map<String, Object> value) throws IOException {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.ALTERNATIVE);
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                Object obj = entry.getValue();
                if (obj instanceof File) {
                    File file = (File) obj;
                    builder.addFormDataPart(entry.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse(FileLoadHelper.getFileMimeType(file.getPath())), file));
                } else {
                    builder.addFormDataPart(entry.getKey(), obj == null ? "" : obj.toString());
                }
            }
            
            if (EventBus.get(PROGRESS) == null) {
                return builder.build();
            } else {
                EventPoster poster = EventBus.poster(PROGRESS);
                return new FileRequestBody(
                        builder.build(), 
                        (current, total, isDone) -> poster.setValues(current, total, isDone).postTo(ThreadMode.MAIN)
                );
            }
            
        }
    } 

    public static class FileUploadConverterFactory extends Converter.Factory {
        
        @Nullable
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            for(Annotation annotation : methodAnnotations) {
                if(annotation instanceof UploadFile) {
                    return new FileRequestBodyConverter();
                }
            }
            return null;
        }
    }
    
}

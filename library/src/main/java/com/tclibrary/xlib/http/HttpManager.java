package com.tclibrary.xlib.http;

import com.tclibrary.xlib.view.HttpProgressDialogHelper;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by FunTc on 2018/10/24.
 */
public class HttpManager {

    private static final class InstanceHolder {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager instance() {
        return InstanceHolder.INSTANCE;
    }

    private Map<String, Object> mRetrofitServiceCache;
    private Map<String, String> mServiceUrls;

    private String mDefaultURL;
    private IHttpConfig mHttpConfig;
    private OkHttpClient mOkHttpClient;


    private HttpManager() {
        mRetrofitServiceCache = new ArrayMap<>();
        mServiceUrls = new ArrayMap<>();
    }


    /**
     * 定义默认的全局BaseUrl，和网络配置
     * @param defaultURL 全局BaseUrl
     * @param httpConfig 配置
     */
    public void init(String defaultURL, @NonNull IHttpConfig httpConfig){
        mDefaultURL = defaultURL;
        mHttpConfig = httpConfig;
    }

    public void init(String defaultURL){
        init(defaultURL, new DefaultHttpConfig());
    }

    public void init(@NonNull IHttpConfig httpConfig){
        init(null, httpConfig);
    }

    public void setDefaultURL(String defaultURL){
        mDefaultURL = defaultURL;
    }

    public void setHttpConfig(@NonNull IHttpConfig httpConfig){
        mHttpConfig = httpConfig;
    }

    public <T> void setServiceURL(Class<T> serviceClz, String url) {
        String key = serviceClz.getCanonicalName();
        mServiceUrls.put(key, url);
        Object service = mRetrofitServiceCache.get(key);
        if (service != null) {
            service = createRetrofit(serviceClz).create(serviceClz);
            mRetrofitServiceCache.put(key, service);
        }
    }

    public String getServiceURL() {
        return getServiceURL(null);
    }

    public String getServiceURL(Class<?> serviceClz) {
        if (serviceClz == null) {
            return mDefaultURL;
        } else {
            String serviceURL = mServiceUrls.get(serviceClz.getCanonicalName());
            if (serviceURL == null) {
                serviceURL = findBaseUrlBy(serviceClz);
            }
            if (serviceURL == null) {
                serviceURL = mDefaultURL;
            }
            return serviceURL;
        }
    }

    /**
     * 根据Api的接口类获得其retrofit处理后的实例
     * @param service Api接口类
     * @param <T> 接口类泛型
     * @return 接口类的实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getRetrofitService(final Class<T> service) {
        Object retrofitService = mRetrofitServiceCache.get(service.getCanonicalName());
        if (retrofitService == null){
            retrofitService = createRetrofit(service).create(service);
            mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
        }
        return (T) retrofitService;
    }

    private Retrofit createRetrofit(final Class<?> service){
        if (mOkHttpClient == null) createOkHttpClient();
        String serviceURL = getServiceURL(service);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(serviceURL).client(mOkHttpClient);
        IHttpConfig config = mHttpConfig == null ? new DefaultHttpConfig() : mHttpConfig;
        config.onRetrofitConfig(builder);
        return builder.build();
    }

    private void createOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        IHttpConfig config = mHttpConfig == null ? new DefaultHttpConfig() : mHttpConfig;
        config.onOkHttpClientConfig(builder);
        mOkHttpClient = builder.build();
    }

    private String findBaseUrlBy(Class<?> clz){
        BaseURL urlAnnotation = clz.getAnnotation(BaseURL.class);
        if (urlAnnotation != null) {
            return urlAnnotation.value();
        }
        return null;
    }

    public OkHttpClient getOkHttpClient(){
        if (mOkHttpClient == null) createOkHttpClient();
        return mOkHttpClient;
    }

    public static <T> ObservableTransformer<T, T> switchThread(){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> showProgress(){
        return showProgress(null);
    }

    public static <T> ObservableTransformer<T, T> showProgress(final CharSequence msg){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        HttpProgressDialogHelper.instance().show(msg);
                    }
                }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        HttpProgressDialogHelper.instance().dismiss();
                    }
                });
            }
        };
    }

}
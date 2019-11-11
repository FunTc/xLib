package com.tclibrary.xlib.http;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.tclibrary.xlib.view.HttpProgressDialogHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
 * Created by TianCheng on 2018/10/24.
 */
public class HttpManager {

	private static final class InstanceHolder {
		private static final HttpManager INSTANCE = new HttpManager();
	}

	public static HttpManager instance() {
		return InstanceHolder.INSTANCE;
	}

	private Map<String, Object> mRetrofitServiceCache;

	private String mDefaultDomain;
	private IHttpConfig mHttpConfig;
	private OkHttpClient mOkHttpClient;
	

	private HttpManager() { }


	/**
	 * 定义默认的全局BaseUrl，和网络配置
	 * @param defaultDomain 全局BaseUrl
	 * @param httpConfig 配置
	 */
	public void init(String defaultDomain, @NonNull IHttpConfig httpConfig){
		mDefaultDomain = defaultDomain;
		mHttpConfig = httpConfig;
	}

	public void init(String defaultDomain){
		init(defaultDomain, new DefaultHttpConfig());
	}
	
	public void init(@NonNull IHttpConfig httpConfig){
		init(null, httpConfig);
	}

	public void setDefaultDomain(String defaultDomain){
		mDefaultDomain = defaultDomain;
	}
	
	public void setHttpConfig(@NonNull IHttpConfig httpConfig){
		mHttpConfig = httpConfig;
	}

	/** 
	 * 根据Api的接口类获得其retrofit处理后的实例
	 * @param service Api接口类
	 * @param <T> 接口类泛型
	 * @return 接口类的实例
	 */
	public <T> T getRetrofitService(final Class<T> service){
		if (mRetrofitServiceCache == null)
			mRetrofitServiceCache = new HashMap<>();
		@SuppressWarnings("unchecked")
		T retrofitService = (T) mRetrofitServiceCache.get(service.getCanonicalName());
		if (retrofitService == null){
			retrofitService = createRetrofit(service).create(service);
			mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
		}
		return retrofitService;
	}

	private Retrofit createRetrofit(final Class service){
		if (mOkHttpClient == null) createOkHttpClient();
		String serviceDomain = findBaseUrlBy(service);
		serviceDomain = StringUtils.isEmpty(serviceDomain) ? mDefaultDomain : serviceDomain;
		Retrofit.Builder builder = new Retrofit.Builder()
				.baseUrl(serviceDomain).client(mOkHttpClient);
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

	private String findBaseUrlBy(Class clz){
		String domain = "";
		for (Field f : clz.getDeclaredFields()){
			boolean hasBaseDomain = f.isAnnotationPresent(BaseURL.class);
			if (hasBaseDomain){
				try {
					domain = (String) f.get(clz);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return domain;
	}
	
	public OkHttpClient getOkHttpClient(){
		if (mOkHttpClient == null) createOkHttpClient();
		return mOkHttpClient;
	}

	public static <T> ObservableTransformer<T, T> switchThread(){
		return new ObservableTransformer<T, T>() {
			@Override
			public ObservableSource<T> apply(Observable<T> upstream) {
				return upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
						.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
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
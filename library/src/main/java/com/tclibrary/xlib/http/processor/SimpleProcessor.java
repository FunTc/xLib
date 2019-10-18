package com.tclibrary.xlib.http.processor;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;

import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.eventbus.OnEventProcessor;
import com.tclibrary.xlib.http.HttpManager;
import com.tclibrary.xlib.view.HttpProgressDialogHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by TianCheng on 2018/11/2.
 * 
 * <p>最多可传递3个参数：
 * <pre>
 * (1).需要传给服务器的参数 {@link RequestParams}；
 * (2).是否显示进度框（不传默认为显示）；
 * (3).显示进度的提示信息
 * </pre>
 * <p>其中传递给服务器的参数为普通的参数时，顺序为调用接口方法中参数的顺序
 * 	<pre>
 * 	例如：Call&lt;User&gt; login(String name, String psw)
 * 	则对应的参数为：RequestParams.create(String name, String psw)
 * 	</pre>
 * 	<p>传递给服务器参数为键值对类型时，参数可以为 {@link Pair} 或 {@link Map}，参数少可以用{@link Pair}参数多可以{@link Map}，
 * 	建议1~3个键值对参数使用{@link Pair}，多于3个键值对使用{@link Map}。
 * 	注：对应接口方法参数必须为{@link Map}形如：Call&lt;User&gt; login(Map&lt;String, Object&gt; params)
 * 	<pre>
 * 	例如：RequestParams.create(Pair.create("name", "admin"), Pair.create("psw", "123456"))
 * 	</pre>
 */
public class SimpleProcessor implements OnEventProcessor {
	
	private Class<?> mApiClass;
	private String mMethodName;

	/**
	 * @param apiClass API接口类
	 * @param methodName 需要调用的方法名
	 */
	public SimpleProcessor(@NonNull Class<?> apiClass, @NonNull String methodName){
		mApiClass = apiClass;
		mMethodName = methodName;
		
	}
	
	@Override
	public void onProcessEvent(@NonNull Event event) throws Exception {
		boolean isShowProgress = event.findParam(Boolean.class, true);
		String progressMsg = event.findParam(String.class);
		if (isShowProgress){
			HttpProgressDialogHelper.instance().show(progressMsg);
		}
		try {
			RequestParams requestParams = event.findParam(RequestParams.class);
			Object api = HttpManager.instance().getRetrofitService(mApiClass);
			Call call;
			if (requestParams == null){
				Method method = mApiClass.getMethod(mMethodName);
				call = (Call) method.invoke(api);
			} else {
				Map<String, Object> mapParams = getMapFromParams(requestParams.params);
				List<Pair> pairParams = getPairsFromParams(requestParams.params);
				if (mapParams.size() != 0 && pairParams.size() != 0 || pairParams.size() != 0) {
					for (Pair pair : pairParams){
						mapParams.put((String) pair.first, pair.second);
					}
					Method method = mApiClass.getMethod(mMethodName, Map.class);
					call = (Call) method.invoke(api, mapParams);
				} else if (mapParams.size() != 0 ) {
					Method method = mApiClass.getMethod(mMethodName, Map.class);
					call = (Call) method.invoke(api, mapParams);
				} else {
					Class[] classType = new Class[requestParams.params.length];
					for (int i = 0; i < requestParams.params.length; i++){
						classType[i] = requestParams.params[i].getClass();
					}
					Method method = mApiClass.getMethod(mMethodName, classType);
					call = (Call) method.invoke(api, requestParams.params);
				}
			}
			Response response = call.execute();
			event.setIsSuccess(response.isSuccessful());
			event.addReturnParam(response.body());
		} finally {
			if (isShowProgress){
				HttpProgressDialogHelper.instance().dismiss();
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getMapFromParams(Object[] params){
		for (Object o : params){
			if (o instanceof Map){
				return (Map<String, Object>) o;
			}
		}
		return new ArrayMap<>();
	}
	
	private static List<Pair> getPairsFromParams(Object[] params){
		List<Pair> list = new ArrayList<>();
		for (Object o : params){
			if (o instanceof Pair){
				list.add((Pair) o);
			}
		}
		return list;
	}
	
}

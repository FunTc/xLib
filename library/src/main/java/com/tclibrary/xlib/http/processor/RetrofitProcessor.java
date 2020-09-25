package com.tclibrary.xlib.http.processor;

import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.http.HttpManager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by FunTc on 2018/11/2.
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
public class RetrofitProcessor extends BaseHttpProcessor {

    private Class<?> mApiClass;
    private String mMethodName;
    private Call<?> mCallTask;
    private boolean isCanceled;

    /**
     * @param apiClass API接口类
     * @param methodName 需要调用的方法名
     */
    public RetrofitProcessor(@NonNull Class<?> apiClass, @NonNull String methodName){
        mApiClass = apiClass;
        mMethodName = methodName;
    }

    @Override
    protected void onProcessInternal(@NonNull Event event) throws Exception {
        RequestParams requestParams = event.findValue(RequestParams.class);
        Object api = HttpManager.instance().getRetrofitService(mApiClass);
        if (requestParams == null){
            Method method = mApiClass.getMethod(mMethodName);
            mCallTask = (Call<?>) method.invoke(api);
        } else {
            Map<String, Object> mapParams = getMapFromParams(requestParams.getParams());
            List<Pair<String, Object>> pairParams = getPairsFromParams(requestParams.getParams());
            if (mapParams.size() != 0 && pairParams.size() != 0 || pairParams.size() != 0) {
                for (Pair<String, Object> pair : pairParams){
                    mapParams.put(pair.first, pair.second);
                }
                Method method = mApiClass.getMethod(mMethodName, Map.class);
                mCallTask = (Call<?>) method.invoke(api, mapParams);
            } else if (mapParams.size() != 0 ) {
                Method method = mApiClass.getMethod(mMethodName, Map.class);
                mCallTask = (Call<?>) method.invoke(api, mapParams);
            } else {
                Class<?>[] classType = new Class[requestParams.getParams().length];
                for (int i = 0; i < requestParams.getParams().length; i++){
                    classType[i] = requestParams.getParam(i).getClass();
                }
                Method method = mApiClass.getMethod(mMethodName, classType);
                mCallTask = (Call<?>) method.invoke(api, requestParams.getParams());
            }
        }
        if (mCallTask == null) {
            event.setIsSuccess(false);
            return;
        }
        Response<?> response = mCallTask.execute();
        if (isCanceled) {
            event.setIsSuccess(false);
        } else {
            event.setIsSuccess(response.isSuccessful());
            event.addProcessedValue(response.body());
        }
    }

    @Override
    public void cancel(boolean mayInterruptIfRunning) {
        isCanceled = true;
        if (mayInterruptIfRunning && mCallTask != null) {
            mCallTask.cancel();
        }
    }

}

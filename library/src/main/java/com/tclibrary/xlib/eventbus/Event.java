package com.tclibrary.xlib.eventbus;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FunTc on 2018/8/31.
 */
public class Event {
	
	private String mEventTag;
	private Object[] mParams;
	private List<Object> mReturnParams;
	private OnEventProcessor mEventProcessor;
	private List<OnEventListener> mEventListeners;
	private Exception mException;
	private boolean isRunning;
	private ThreadMode mProcessThreadMode;
	private ThreadMode mObserveThreadMode;
	private boolean isSuccess;
	
	private List<OnEventListener> mEventListenersAddCache;
	private List<OnEventListener> mEventListenersRemoveCache;
	
	
	Event(@NonNull String eventTag){
		mEventTag = eventTag;
		mEventListeners = new ArrayList<>();
	}
	
	public String getEventTag(){
		return mEventTag;
	}
	
	public boolean eventTagIs(String eventTag){
		return !TextUtils.isEmpty(eventTag) && TextUtils.equals(mEventTag, eventTag);
	}
	
	public boolean eventTagIs(int eventTag){
		return eventTagIs(String.valueOf(eventTag));
	}
	
	public void addReturnParam(Object param){
		if (mReturnParams == null){
			mReturnParams = new ArrayList<>();
		}
		mReturnParams.add(param);
	}
	
	public List<Object> getReturnParams(){
		return mReturnParams;
	}
	
	void setParams(Object... params){
		mParams = params;
	}
	
	public Object getParam(int index){
		if (mParams != null && index < mParams.length){
			return mParams[index];
		}
		return null;
	}

	public Object getReturnParams(int index) {
		if (mReturnParams != null && index < mReturnParams.size()){
			return mReturnParams.get(index);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findParam(Class<T> cls){
		if(mParams != null){
			for(Object obj : mParams){
				if(cls.isInstance(obj)){
					return (T)obj;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T findParam(Class<T> cls, T defaultValue){
		if(mParams != null){
			for(Object obj : mParams){
				if(cls.isInstance(obj)){
					return (T)obj;
				}
			}
		}
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T findReturnParam(Class<T> cls){
		if(mReturnParams != null){
			for(Object obj : mReturnParams){
				if(cls.isInstance(obj)){
					return (T)obj;
				}
			}
		}
		return null;
	}

	public void setIsSuccess(boolean isSuccess){
		this.isSuccess = isSuccess;
	}
	
	public boolean isSuccess() {
		 return isSuccess;
	}

	void addEventListener(OnEventListener listener){
		if (isRunning){
			if (mEventListenersAddCache == null) mEventListenersAddCache = new ArrayList<>();
			if (!mEventListenersAddCache.contains(listener)) mEventListenersAddCache.add(listener);
		} else {
			if (!mEventListeners.contains(listener))
				mEventListeners.add(listener);
		}
	}
	
	void removeEventListener(OnEventListener listener){
		if (isRunning){
			if (mEventListenersRemoveCache == null) mEventListenersRemoveCache = new ArrayList<>();
			if (!mEventListenersRemoveCache.contains(listener)) mEventListenersRemoveCache.add(listener);
		} else {
			mEventListeners.remove(listener);
		}
	}
	
	void setEventProcessor(OnEventProcessor processor){
		mEventProcessor = processor;
	}
	
	OnEventProcessor getEventProcessor(){
		return mEventProcessor;
	}
	
	List<OnEventListener> getEventListeners(){
		return mEventListeners;
	}
	
	private boolean isStopped = false;
	
	void executeEventProcessor(){
		if (mEventProcessor == null || isRunning) return;
		isRunning = true;
		try {
			mEventProcessor.onProcessEvent(this);
		} catch (Exception e) {
			setException(e);
			e.printStackTrace();
		}
		isRunning = false;
	}
	
	void callEventListeners(){
		if (mEventListeners == null || mEventListeners.isEmpty()) return;
		isRunning = true;
		for (OnEventListener listener : mEventListeners){
			if(isStopped){
				isStopped = false;
				break;
			}
			listener.onEventResult(this);
		}
		isRunning = false;
		
		if (mEventListenersAddCache != null && mEventListenersAddCache.size() > 0){
			for (OnEventListener listener : mEventListenersAddCache){
				listener.onEventResult(this);
			}
			mEventListeners.addAll(mEventListenersAddCache);
			mEventListenersAddCache.clear();
		}
		if (mEventListenersRemoveCache != null && mEventListenersRemoveCache.size() > 0){
			mEventListeners.removeAll(mEventListenersRemoveCache);
			mEventListenersRemoveCache.clear();
		}
	}
	
	void setException(Exception e){
		mException = e;
	}

	public Exception getException() {
		return mException;
	}
	
	boolean isRunning(){
		return isRunning;
	}

	/**
	 * 设置是否终止运行
	 */
	void stopEvent(){
		isStopped = true;
	}
	
	void setProcessThreadMode(ThreadMode threadMode){
		mProcessThreadMode = threadMode;
	}
	
	void setObserveThreadMode(ThreadMode threadMode){
		mObserveThreadMode = threadMode;
	}
	
	ThreadMode getProcessThreadMode(){
		return mProcessThreadMode;
	}
	
	ThreadMode getObserveThreadMode(){
		return mObserveThreadMode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Event){
			Event e = (Event)obj;
			return hashCode() == e.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mEventTag.hashCode() + super.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
				.append("{")
				.append("\"eventTag\"：").append(mEventTag).append(", ")
				.append("\"params\"：[");
		if (mParams != null){
			for (int i = 0; i < mParams.length; i++){
				builder.append(mParams[i]);
				if (i < mParams.length - 1) builder.append(", ");
			}
		}
		builder.append("], \"returnParams\"：[");
		if (mReturnParams != null){
			for (Object o : mReturnParams){
				builder.append(o);
				if (mReturnParams.indexOf(o) < mReturnParams.size() -1 ) builder.append(", ");
			}	
		} 
		builder.append("]}");
		return builder.toString();
	}
}

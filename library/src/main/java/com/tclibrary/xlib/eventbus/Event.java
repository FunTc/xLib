package com.tclibrary.xlib.eventbus;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/8/31.
 */
public class Event {
	
	private String mEventTag;
	private Object[] mValues;
	private List<Object> mProcessedValues;
	private Exception mException;
	private boolean isPosting;
	private boolean isSuccess;
	private boolean isCanceled;
	
	
	Event(@NonNull String eventTag){
		mEventTag = eventTag;
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
	
	public void addProcessedValue(Object value){
		if (mProcessedValues == null){
			mProcessedValues = new ArrayList<>();
		}
		mProcessedValues.add(value);
	}
	
	public List<Object> getProcessedValues(){
		return mProcessedValues;
	}
	
	void setValues(Object... values){
		mValues = values;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(int index){
		if (mValues != null && index < mValues.length){
			return (T)mValues[index];
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProcessedValue(int index) {
		if (mProcessedValues != null && index < mProcessedValues.size()){
			return (T)mProcessedValues.get(index);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findValue(Class<T> cls){
		if(mValues != null){
			for(Object obj : mValues){
				if(cls.isInstance(obj)){
					return (T)obj;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T findValue(Class<T> cls, T defaultValue){
		T value = findValue(cls);
		if (value != null) return value;
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T findProcessedValue(Class<T> cls){
		if(mProcessedValues != null){
			for(Object obj : mProcessedValues){
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
	
	
	void setException(Exception e){
		mException = e;
	}

	public Exception getException() {
		return mException;
	}
	
	public boolean isPosting(){
		return isPosting;
	}
	
	void setIsPosting(boolean isPosting) {
		this.isPosting = isPosting;
	}
	
	public boolean isCanceled() {
		return isCanceled;
	}
	
	void init() {
		this.isCanceled = false;
		this.isPosting = false;
		this.isSuccess = false;
		if (mProcessedValues != null) {
			mProcessedValues.clear();
		}
	}
	
	void cancel() {
		isCanceled = true;
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

	@NonNull
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
				.append("{")
				.append("\"eventTag\"：").append(mEventTag).append(", ")
				.append("\"values\"：[");
		if (mValues != null){
			for (int i = 0; i < mValues.length; i++){
				builder.append(mValues[i]);
				if (i < mValues.length - 1) builder.append(", ");
			}
		}
		builder.append("], \"returnValues\"：[");
		if (mProcessedValues != null){
			for (Object o : mProcessedValues){
				builder.append(o);
				if (mProcessedValues.indexOf(o) < mProcessedValues.size() -1 ) builder.append(", ");
			}	
		} 
		builder.append("]}");
		return builder.toString();
	}
	
}

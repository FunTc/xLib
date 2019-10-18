package com.tclibrary.xlib.eventbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by TianCheng on 2018/8/30.
 */
public class EventBus {
	
	private static class InstanceHolder{
		private static final EventBus INSTANCE = new EventBus();
	} 
	
	public static EventBus instance(){
		return InstanceHolder.INSTANCE;
	}

	private EventBus(){ }
	
	private EventDispatcher mEventDispatcher = new EventDispatcher();

	private ConcurrentHashMap<String, Event> mTag2EventMap = new ConcurrentHashMap<>();
	
	
	public void removeEvent(int eventTag){
		removeEvent(String.valueOf(eventTag));
	}
	
	public void removeEvent(@NonNull String eventTag){
		mTag2EventMap.remove(eventTag);
	}
	
	public void removeEventListener(int eventTag, OnEventListener eventListener){
		removeEventListener(String.valueOf(eventTag), eventListener);
	}
	
	public void removeEventListener(@NonNull String eventTag, OnEventListener eventListener){
		Event event = mTag2EventMap.get(eventTag);
		if (event != null){
			if (event.getEventListeners().size() > 1){
				event.removeEventListener(eventListener);
			} else {
				removeEvent(eventTag);
			}
		} else {
			try {
				throw new Exception("you didn't register an Event with eventTag as " + eventTag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopEvent(int eventTag){
		stopEvent(String.valueOf(eventTag));
	}
	
	public void stopEvent(@NonNull String eventTag){
		if (mTag2EventMap.containsKey(eventTag)){
			Event event = mTag2EventMap.get(eventTag);
			if (event.isRunning()){
				event.stopEvent();
			} else {
				Logger.w("The Event is not running !");
			}
		} else {
			try {
				throw new Exception("you didn't register an Event with eventTag as " + eventTag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@NonNull
	public static EventRegister add(int eventTag){
		return add(String.valueOf(eventTag));
	}
	
	@NonNull
	public static EventRegister add(@NonNull String eventTag){
		return new RegisterImpl(eventTag);
	}

	private static class RegisterImpl implements EventRegister{
		
		private String eventTag;
		private OnEventProcessor processor;
		private OnEventListener listener;

		RegisterImpl(String eventTag){
			this.eventTag = eventTag;
		}

		@Override
		public EventRegister setEventProcessor(@NonNull OnEventProcessor processor) {
			this.processor = processor;
			return this;
		}

		@Override
		public EventRegister addEventListener(@NonNull OnEventListener listener) {
			this.listener = listener;
			return this;
		}

		@Override
		public void register(Object object) {
			Event event = instance().mTag2EventMap.putIfAbsent(eventTag, new Event(eventTag));
			if (event == null){
				event = instance().mTag2EventMap.get(eventTag);
			}
			if (processor != null) event.setEventProcessor(processor);
			if (listener != null) event.addEventListener(listener);
			if (object instanceof LifecycleOwner){
				((LifecycleOwner)object).getLifecycle().addObserver(new AutoUnregisterEvent(object, eventTag, listener));
			} else {
				try {
					throw new Exception("you need to unregister the EventListener on " + object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			processor = null;
			listener = null;
		}

		@Override
		public EventPoster registerAt(Object object) {
			register(object);
			return new PosterImpl(eventTag);
		}

	}
	
	@NonNull
	public static EventPoster get(int eventTag){
		return get(String.valueOf(eventTag));
	}
	
	@NonNull
	public static EventPoster get(@NonNull String eventTag){
		if (instance().mTag2EventMap.containsKey(eventTag)){
			return new PosterImpl(eventTag);
		} else {
			throw new NoSuchElementException("The eventTag \"" + eventTag + "\" is not registered");
		}
	}
	
	private static class PosterImpl implements EventPoster{

		private String eventTag;

		PosterImpl(String eventTag){
			this.eventTag = eventTag;
			//重置线程模式
			instance().mTag2EventMap.get(eventTag).setProcessThreadMode(null);
			instance().mTag2EventMap.get(eventTag).setObserveThreadMode(null);
		}

		@Override
		public EventPoster addParams(Object... params) {
			instance().mTag2EventMap.get(eventTag).setParams(params);
			return this;
		}

		@Override
		public EventPoster processOn(ThreadMode threadMode) {
			instance().mTag2EventMap.get(eventTag).setProcessThreadMode(threadMode);
			return this;
		}

		@Override
		public EventPoster observeOn(ThreadMode threadMode) {
			instance().mTag2EventMap.get(eventTag).setObserveThreadMode(threadMode);
			return this;
		}

		@Override
		public void post() {
			final Event event = instance().mTag2EventMap.get(eventTag);
			instance().mEventDispatcher.dispatchEvent(event);
		}

		@Override
		public void post(final long delay) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					post();
				}
			}).start();
		}
	}
	
	private static class AutoUnregisterEvent implements LifecycleObserver{
		private Object object;
		private String eventTag;
		private OnEventListener eventListener;

		AutoUnregisterEvent(Object object, String eventTag, OnEventListener eventListener){
			this.object = object;
			this.eventTag = eventTag;
			this.eventListener = eventListener;
		}
		
		@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 
		void onDestroy(){
			instance().removeEventListener(eventTag, eventListener);
			/*((LifecycleOwner)object).getLifecycle().removeObserver(this);
			object = null;*/
			eventListener = null;
		}
		
	}

	private class EventDispatcher {

		private ExecutorService mExecutorService = Executors.newCachedThreadPool();

		private Handler mMainHandler = new Handler(Looper.getMainLooper());

		void dispatchEvent(final Event event){
			ThreadMode processorThread = event.getProcessThreadMode();
			if (processorThread == null || processorThread == ThreadMode.ASYNC){
				processOnAsync(event);
			} else if (processorThread == ThreadMode.MAIN){
				processOnMain(event);
			}
		}

		private void processOnAsync(final Event event){
			mExecutorService.execute(new Runnable() {
				@Override
				public void run() {
					event.executeEventProcessor();
					if (event.getObserveThreadMode() == ThreadMode.ASYNC) {
						event.callEventListeners();
					}else {
						mMainHandler.post(new Runnable() {
							@Override
							public void run() {
								event.callEventListeners();
							}
						});
					}
				}
			});
		}

		private void processOnMain(final Event event){
			if (isMainThread()){
				processOnMainInternal(event);
			} else {
				mMainHandler.post(new Runnable() {
					@Override
					public void run() {
						processOnMainInternal(event);
					}
				});
			}
		}

		private void processOnMainInternal(final Event event){
			event.executeEventProcessor();
			if (event.getObserveThreadMode() == ThreadMode.ASYNC) {
				mExecutorService.execute(new Runnable() {
					@Override
					public void run() {
						event.callEventListeners();
					}
				});
			} else {
				event.callEventListeners();
			}
		}

		private boolean isMainThread(){
			return Looper.getMainLooper().getThread() == Thread.currentThread();
		}
	}
	
}

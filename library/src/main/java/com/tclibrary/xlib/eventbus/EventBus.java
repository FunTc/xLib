package com.tclibrary.xlib.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Created by FunTc on 2018/8/30.
 */
public class EventBus {
	
	private static class InstanceHolder{
		private static final EventBus INSTANCE = new EventBus();
	} 
	
	public static EventBus instance(){
		return InstanceHolder.INSTANCE;
	}

	private EventBus(){ }
	
	private ConcurrentHashMap<String, Event> mTag2EventMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, CopyOnWriteArrayList<EventListenerWrapper>> mTag2ListenersMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, EventProcessor> mTag2ProcessorMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Future<?>> mTag2FutureTaskMap = new ConcurrentHashMap<>();

	private ExecutorService mExecutorService = Executors.newCachedThreadPool();
	private Handler mMainHandler = new Handler(Looper.getMainLooper());
	
	
	private boolean isMainThread(){
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
	
	private void processEvent(Event event, ThreadMode observerThreadMode) {
		String tag = event.getEventTag();
		EventProcessor processor = mTag2ProcessorMap.get(tag);
		if (processor != null) {
			if (processor.isAsync()) {
				Future<?> future = mExecutorService.submit(() -> processEventInternal(event, processor, observerThreadMode));
				mTag2FutureTaskMap.put(tag, future);
			} else {
				if (isMainThread()) {
					processEventInternal(event, processor, observerThreadMode);
				} else {
					mMainHandler.post(() -> processEventInternal(event, processor, observerThreadMode));
				}
			}
		} else {
			dispatchEvent(event, observerThreadMode);
		}
	}
	
	private void processEventInternal(Event event, @NonNull EventProcessor processor, ThreadMode observerThreadMode) {
		try {
			processor.onProcess(event);
		} catch (Exception e) {
			event.setIsSuccess(false);
			event.setException(e);
			e.printStackTrace();
		}
		mTag2FutureTaskMap.remove(event.getEventTag());
		if (event.isCanceled()) {
			event.setIsPosting(false);
		} else {
			dispatchEvent(event, observerThreadMode);
		}
	}
	
	private void dispatchEvent(Event event, ThreadMode observerThreadMode) {
		if (observerThreadMode == ThreadMode.MAIN) {
			if (isMainThread()) {
				dispatchEventInternal(event);
			} else {
				mMainHandler.post(() -> dispatchEventInternal(event));
			}
		} else {
			if (isMainThread()) {
				Future<?> future = mExecutorService.submit(() -> dispatchEventInternal(event));
				mTag2FutureTaskMap.put(event.getEventTag(), future);
			} else {
				dispatchEventInternal(event);
			}
		}
	}
	
	private void dispatchEventInternal(Event event) {
		List<EventListenerWrapper> listenerWrappers = mTag2ListenersMap.get(event.getEventTag());
		if (listenerWrappers != null && listenerWrappers.size() != 0) {
			for (EventListenerWrapper listenerWrapper : listenerWrappers) {
				if (listenerWrapper.getListener() != null) {
					if (listenerWrapper.isJustNotifyInActive()) {
						if (listenerWrapper.isActive()) {
							listenerWrapper.getListener().onEventResult(event);
						}
					} else {
						listenerWrapper.getListener().onEventResult(event);
					}
				}
			}
		} 
		event.setIsPosting(false);
		mTag2FutureTaskMap.remove(event.getEventTag());
	}

	public void removeEvent(int eventTag){
		removeEvent(String.valueOf(eventTag));
	}

	public void removeEvent(@NonNull String eventTag){
		mTag2EventMap.remove(eventTag);
		mTag2ProcessorMap.remove(eventTag);
		List<EventListenerWrapper> listeners = mTag2ListenersMap.remove(eventTag);
		if (listeners != null) {
			for (EventListenerWrapper listener : listeners) {
				listener.release();
			}
			listeners.clear();
		}
	}

	public void removeEventListener(int eventTag, OnEventListener eventListener){
		removeEventListener(String.valueOf(eventTag), eventListener);
	}

	public void removeEventListener(@NonNull String eventTag, OnEventListener listener){
		List<EventListenerWrapper> listenerWrappers = mTag2ListenersMap.get(eventTag);
		if (listenerWrappers != null){
			EventListenerWrapper elw = null;
			for (EventListenerWrapper listenerWrapper: listenerWrappers) {
				if (listenerWrapper.getListener() == listener) {
					elw = listenerWrapper;
					break;
				}
			}
			if (elw != null) {
				elw.release();
				listenerWrappers.remove(elw);
			} else {
				try {
					throw new IllegalArgumentException("there is no EventListener[" + listener + "] registered for the Event");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (listenerWrappers.size() == 0) {
				removeEvent(eventTag);
			}
		} else {
			try {
				throw new IllegalArgumentException("you didn't register the EventListener[" + listener + "] for the Event with eventTag=" + eventTag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void removeEventListener(@NonNull String eventTag, EventListenerWrapper listenerWrapper) {
		List<EventListenerWrapper> listeners = mTag2ListenersMap.get(eventTag);
		if (listeners != null) {
			if (listeners.size() > 0) {
				listenerWrapper.release();
				listeners.remove(listenerWrapper);
			}
			if (listeners.size() == 0) {
				removeEvent(eventTag);
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

	public static Event get(int eventTag){
		return get(String.valueOf(eventTag));
	}

	public static Event get(@NonNull String eventTag){
		return instance().mTag2EventMap.get(eventTag);
	}

	@NonNull
	public static EventPoster poster(int eventTag){
		return poster(String.valueOf(eventTag));
	}

	@NonNull
	public static EventPoster poster(@NonNull String eventTag){
		Event event = instance().mTag2EventMap.get(eventTag);
		if (event == null) {
			event = new Event(eventTag);
		}
		return new PosterImpl(event);
	}

	public static void cancelEvent(int eventTag) {
		cancelEvent(String.valueOf(eventTag));
	}
	
	public static void cancelEvent(@NonNull String eventTag) {
		Event event = get(eventTag);
		if (event != null) {
			event.cancel();
			EventProcessor processor = instance().mTag2ProcessorMap.get(eventTag);
			if (processor != null) {
				processor.cancel(true);
			}
			Future<?> future = instance().mTag2FutureTaskMap.get(eventTag);
			if (future != null) {
				future.cancel(true);
			}
		}
	}

	private static class RegisterImpl implements EventRegister{
		
		private String eventTag;
		private Event event;
		private EventProcessor processor;
		private OnEventListener listener;
		private boolean justNotifyInActive;

		RegisterImpl(String eventTag){
			this.eventTag = eventTag;
		}

		@Override
		public EventRegister setEventProcessor(@NonNull EventProcessor processor) {
			this.processor = processor;
			return this;
		}

		@Override
		public EventRegister addEventListener(@NonNull OnEventListener listener) {
			this.listener = listener;
			return this;
		}

		@Override
		public EventRegister addEventListener(@NonNull OnEventListener listener, boolean justNotifyInActive) {
			this.listener = listener;
			this.justNotifyInActive = justNotifyInActive;
			return this;
		}

		@Override
		public void register(Object object) {
			event = instance().mTag2EventMap.putIfAbsent(eventTag, new Event(eventTag));
			if (event == null){
				event = instance().mTag2EventMap.get(eventTag);
			}
			if (processor != null) {
				instance().mTag2ProcessorMap.put(eventTag, processor);
			}
			if (this.listener != null) {
				CopyOnWriteArrayList<EventListenerWrapper> listenerWrappers = instance().mTag2ListenersMap.get(eventTag);
				if (listenerWrappers == null) {
					listenerWrappers = new CopyOnWriteArrayList<>();
					instance().mTag2ListenersMap.put(eventTag, listenerWrappers);
				}
				boolean isAdded = false;
				EventListenerWrapper elw = null;
				for (EventListenerWrapper listenerWrapper: listenerWrappers) {
					if (listenerWrapper.getListener() == this.listener) {
						isAdded = true;
						elw = listenerWrapper;
						elw.setJustNotifyInActive(this.justNotifyInActive);
						break;
					}
				}
				if (!isAdded) {
					elw = new EventListenerWrapper(this.eventTag, this.listener, this.justNotifyInActive);
					listenerWrappers.add(elw);
				}
				
				if (object instanceof LifecycleOwner){
					((LifecycleOwner)object).getLifecycle().addObserver(elw);
				}
			}
			processor = null;
			listener = null;
		}

		@Override
		public EventPoster registerAt(Object object) {
			register(object);
			return new PosterImpl(event);
		}

	}
	
	private static class PosterImpl implements EventPoster{

		private long delay;
		private Event event;

		PosterImpl(Event event){
			this.event = event;
		}

		@Override
		public EventPoster setValues(Object... params) {
			event.setValues(params);
			return this;
		}

		@Override
		public EventPoster delay(long delay) {
			this.delay = delay;
			return this;
		}

		@Override
		public void post() {
			postTo(ThreadMode.MAIN);
		}

		@Override
		public void postTo(ThreadMode mode) {
			if (event.isPosting()) return;
			event.init();
			if (delay > 0) {
				new Thread(() -> {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!event.isCanceled()) {
						event.setIsPosting(true);
						EventBus.instance().processEvent(event, mode);
					}
					event = null;
				}).start();
			} else {
				event.setIsPosting(true);
				EventBus.instance().processEvent(event, mode);
				event = null;
			}
		}
	}
	
	private static class EventListenerWrapper implements LifecycleObserver {
		
		private String eventTag;
		private OnEventListener listener;
		private boolean justNotifyInActive;
		private boolean isActive;

		EventListenerWrapper(@NonNull String eventTag, OnEventListener listener, boolean justNotifyInActive) {
			this.eventTag = eventTag;
			this.listener = listener;
			this.justNotifyInActive = justNotifyInActive;
		}
		
		OnEventListener getListener() {
			return this.listener;
		}
		
		boolean isJustNotifyInActive() {
			return this.justNotifyInActive;
		}
		
		boolean isActive() {
			return this.isActive;
		}
		
		void release() {
			this.listener = null;
		}
		
		void setJustNotifyInActive(boolean justNotifyInActive) {
			this.justNotifyInActive = justNotifyInActive;
		}

		@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
		void onResume() {
			this.isActive = true;
		}

		@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
		void onPause() {
			this.isActive = false;
		}

		@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
		void onDestroy(){
			EventBus.instance().removeEventListener(eventTag, this);
		}
	}
}

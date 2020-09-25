package com.tclibrary.xlib.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

/**
 * Created by FunTc on 2018/11/5.
 */
public class PluginHelper {

    private ArrayList<IPlugin> mPlugins;
    private Map<Class<? extends IPlugin>, Collection<? extends IPlugin>> mPluginsCache;

    public PluginHelper(){

    }

    public void addPlugin(@NonNull IPlugin plugin){
        checkNull();
        if (!mPlugins.contains(plugin)){
            mPlugins.add(plugin);
            mPluginsCache.clear();
        }
    }

    public void removePlugin(@NonNull IPlugin plugin){
        if ( mPlugins == null) return;
        if (mPlugins.remove(plugin)){
            mPluginsCache.clear();
        }
    }

    public void clear(){
        if(mPlugins != null) mPlugins.clear();
        if(mPluginsCache != null) mPluginsCache.clear();
    }

    @SuppressWarnings("unchecked")
    public <E extends IPlugin> Collection<E> getPlugins(@NonNull Class<E> cls){
        checkNull();
        Collection<E> collection = (Collection<E>) mPluginsCache.get(cls);
        if (collection == null) {
            collection = new ArrayList<>();
            for (IPlugin plugin : mPlugins)
                if (cls.isInstance(plugin))
                    collection.add((E) plugin);
            collection = Collections.unmodifiableCollection(collection);
            mPluginsCache.put(cls, collection);
        }
        return collection;
    }

    private void checkNull(){
        if(mPlugins == null){
            mPlugins = new ArrayList<>();
        }
        if(mPluginsCache == null){
            mPluginsCache = new ArrayMap<>();
        }
    }


}

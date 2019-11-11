package com.tclibrary.xlib.http;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by FunTc on 2018/10/30.
 */
public class RequestParamsHandlerImpl implements IRequestParamsHandler {
	
	@NonNull
	@Override
	public Map<String, String> getCommonParams() {
		return new ArrayMap<>();
	}

	@Override
	public void handleParams(@NonNull Map<String, String> params) {

	}
}

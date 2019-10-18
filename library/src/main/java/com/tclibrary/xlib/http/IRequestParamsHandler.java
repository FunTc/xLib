package com.tclibrary.xlib.http;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by TianCheng on 2018/10/25.
 */
public interface IRequestParamsHandler {

	@NonNull Map<String, String> getCommonParams();

	void handleParams(@NonNull Map<String, String> params);
	
}

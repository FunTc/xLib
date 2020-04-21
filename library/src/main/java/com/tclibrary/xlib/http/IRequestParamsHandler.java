package com.tclibrary.xlib.http;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by FunTc on 2018/10/25.
 */
public interface IRequestParamsHandler {

	@NonNull Map<String, String> getCommonParams();

	void handleParams(@NonNull Map<String, String> params);
	
}

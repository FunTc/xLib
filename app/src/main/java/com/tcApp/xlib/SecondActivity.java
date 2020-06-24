package com.tcApp.xlib;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.eventbus.EventBus;
import com.tclibrary.xlib.eventbus.OnEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by FunTc on 2020/03/30.
 */
public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.button).setOnClickListener(this);
        tvInfo = findViewById(R.id.tvInfo);

        EventBus.add("simpleEvent").addEventListener(onEventListener).register(this);
    }

    @Override
    public void onClick(View v) {
        EventBus.poster("simpleEvent").setValues("EventBus Message").post();
    }

    private OnEventListener onEventListener = new OnEventListener() {
        @Override
        public void onEventResult(@NonNull Event event) {
            String info = event.findValue(String.class, "我是没有获取到的默认值");
            tvInfo.setText(info);
        }
    };
    
    
}

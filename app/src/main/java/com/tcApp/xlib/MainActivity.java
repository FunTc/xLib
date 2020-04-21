package com.tcApp.xlib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tcApp.xlib.databinding.ActivityMainBinding;
import com.tclibrary.xlib.eventbus.Event;
import com.tclibrary.xlib.eventbus.EventBus;
import com.tclibrary.xlib.eventbus.OnEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
    ActivityMainBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(viewBinding.getRoot());
        viewBinding.button.setOnClickListener(this);

        EventBus.add("simpleEvent").addEventListener(onEventListener, false).register(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
    
    private OnEventListener onEventListener = new OnEventListener() {
        @Override
        public void onEventResult(@NonNull Event event) {
            String info = event.findValue(String.class, "我是没有获取到的默认值");
            viewBinding.tvInfo.setText(info);
        }
    };
    
}

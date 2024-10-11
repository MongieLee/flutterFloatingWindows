package com.example.fuck;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;

public class IotEventChannelBridge {
    private static IotEventChannelBridge globalInstance;
    private static final String CHANNEL = "cn.mgl.common/event_bridge";
    private final EventChannel eventChannel;
    private EventSink eventSink;
    private final Context context;

    static IotEventChannelBridge getInstance() {
        return globalInstance;
    }

    public IotEventChannelBridge(FlutterEngine flutterEngine, Context context) {
        this.context = context;
        eventChannel = new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);
        eventChannel.setStreamHandler(new StreamHandler() {
            @Override
            public void onListen(Object arguments, EventSink events) {
                eventSink = events;
            }

            @Override
            public void onCancel(Object arguments) {
                eventSink = null;
            }
        });
        globalInstance = this;
    }

    public void emitEvent(String messageType, HashMap<String, Object> arguments) {
        try {
            if (eventSink != null) {
                HashMap<String, Object> flutterParams = new HashMap<>();
                flutterParams.put("messageType", messageType);
                flutterParams.put("params", arguments);
                ((Activity) context).runOnUiThread(() -> {
                    eventSink.success(flutterParams);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
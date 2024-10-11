package com.example.fuck;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class IotMethodChannelBridge implements MethodChannel.MethodCallHandler {

    private static final String CHANNEL = "cn.mgl.common/method_bridge";
    private final MethodChannel methodChannel;
    NativeMethods nativeMethods = new NativeMethods();
    private final Context context;

    public IotMethodChannelBridge(FlutterEngine flutterEngine, Context context) {
        methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);
        methodChannel.setMethodCallHandler(this);
        this.context = context;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        Class<?> clazz = nativeMethods.getClass();
        try {
            Method method = clazz.getMethod(call.method, HashMap.class, HashMap.class);
            HashMap<String, Object> ret = new HashMap<>();
            AtomicBoolean haveReturn = new AtomicBoolean(false);
            method.setAccessible(true);
            new Thread(() -> {
                try {
                    method.invoke(nativeMethods, call.arguments(), ret);
                    if (!haveReturn.get()) {
                        haveReturn.set(true);
                        result.success(ret);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!haveReturn.get()) {
                        haveReturn.set(true);
                        result.notImplemented();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            result.notImplemented();
        }
    }
}

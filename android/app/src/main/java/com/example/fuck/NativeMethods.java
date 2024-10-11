package com.example.fuck;

import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;

public class NativeMethods {
    static final Integer alertWindowsRequestCode = 1007251;
    String LogTag = "NativeMethods";

    public void testNativeInvoke(HashMap<String, Object> args, HashMap<String, Object> ret) {
        ret.put("result", "test");
        IotEventChannelBridge.getInstance().emitEvent("testEvent", ret);
        // emitEvent方法调用只是示例，不需要的话可以不调用，dart层的IotMethodChannel.callNativeMethod方法也会收到回调
    }

    public void checkAlertWindowsPermission(HashMap<String, Object> args, HashMap<String, Object> ret) {
        Boolean isOnlyCheck = true;
        if (args.get("isOnlyCheck") != null && args.get("isOnlyCheck") instanceof Boolean) {
            isOnlyCheck = (Boolean) args.get("isOnlyCheck");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity activity = (Activity) MainActivity.globalContext;
            if (!Settings.canDrawOverlays(activity)) {
                if (Boolean.FALSE.equals(isOnlyCheck) && activity != null) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.globalContext.getPackageName()));
                    activity.startActivityForResult(intent, alertWindowsRequestCode);
                }
                ret.put("hasPermission", false);
            } else {
                ret.put("hasPermission", true);
            }
        } else {
            ret.put("hasPermission", false);
        }
    }

    WindowManager windowManager;
    View inflate;

    public void startUpOverlayWindows(HashMap<String, Object> args, HashMap<String, Object> ret) {
        if (inflate != null && windowManager != null) {
            windowManager.removeView(inflate);
        }
        Activity activity = (Activity) MainActivity.globalContext;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                windowManager = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
                inflate = LayoutInflater.from(activity).inflate(R.layout.layout_floating_window, null);
                int layoutFlag;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
                }
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        layoutFlag,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
                params.gravity = Gravity.TOP | Gravity.LEFT;
                params.x = 0;
                params.y = 100;
                windowManager.addView(inflate, params);
                inflate.setOnTouchListener(new View.OnTouchListener() {
                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        long touchStartTime = 0;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialX = params.x;
                                initialY = params.y;
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                touchStartTime = System.currentTimeMillis();
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(inflate, params);
                                return true;
                            case MotionEvent.ACTION_UP:
                                long touchEndTime = System.currentTimeMillis();
                                long timeDiff = touchStartTime - touchEndTime;
                                float finalTouchX = event.getRawX();
                                float finalTouchY = event.getRawY();
                                if (Math.abs(finalTouchX - initialTouchX) < 10 && Math.abs(finalTouchY - initialTouchY) < 10 && timeDiff < 300) {
                                    IotEventChannelBridge.getInstance().emitEvent("clickFloatingWindowsEvent", ret);
                                }
                        }
                        return false;
                    }
                });
            });
        }
    }
}
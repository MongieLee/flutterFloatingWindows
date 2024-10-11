package com.example.fuck;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {
    private IotMethodChannelBridge iotMethodChannelBridge;
    private IotEventChannelBridge iotEventChannelBridge;
    static Context globalContext;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        iotMethodChannelBridge = new IotMethodChannelBridge(flutterEngine, getContext());
        iotEventChannelBridge = new IotEventChannelBridge(flutterEngine, getContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCoe, Intent data) {
        super.onActivityResult(requestCode, resultCoe, data);
        if (requestCode == NativeMethods.alertWindowsRequestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getContext())) {
                    Toast.makeText(this, "已开启悬浮窗权限", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "依然没有悬浮窗权限", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalContext = this;
    }
}

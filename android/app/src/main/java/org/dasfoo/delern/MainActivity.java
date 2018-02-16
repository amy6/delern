package org.dasfoo.delern;

import android.os.Bundle;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);
        new MethodChannel(getFlutterView(), "org.dasfoo.delern/android").setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("Main")) {
                        SplashScreenActivity.startActivity(getApplicationContext());
                        result.success(null);
                    } else {
                        result.notImplemented();
                    }
                });
    }
}

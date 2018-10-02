package org.dasfoo.delern;

import android.os.Bundle;
import android.provider.Settings;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
    if ("true".equals(testLabSetting)) {
      // Do not enable Firebase Analytics in Firebase Test Lab (e.g. pre-launch report).
      // Documentation on detecting Firebase Test Lab: https://goo.gl/JeHQuW.
      // Documentation on Analytics: https://firebase.google.com/support/guides/disable-analytics.
      FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
    }

    GeneratedPluginRegistrant.registerWith(this);
  }
}

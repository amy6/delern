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
    if (!"true".equals(testLabSetting)) {
      // Re-enable Analytics when not in Firebase Test Lab. See AndroidManifest.xml for details.
      // Documentation on detecting Firebase Test Lab: https://goo.gl/JeHQuW
      FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
    }

    GeneratedPluginRegistrant.registerWith(this);
  }
}

package org.dasfoo.delern;

import android.os.Bundle;
import android.provider.Settings;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
    if (!"true".equals(testLabSetting)) {
      // Re-enable Analytics when not in Firebase Test Lab. See AndroidManifest.xml for details.
      // Documentation on detecting Firebase Test Lab:
      // https://firebase.google.com/docs/test-lab/android/android-studio
      FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
    }

    if (BuildConfig.DEBUG) {
      // TODO(dotdoom): this should be placed in main.dart once it's available in Flutter.
      FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

    GeneratedPluginRegistrant.registerWith(this);
  }
}

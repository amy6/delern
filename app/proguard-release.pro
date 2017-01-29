# Add project specific ProGuard rules here.
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Disable logging below "error" (assuming this is only enabled on Release).
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Keep original names for Firebase serialization:
# https://firebase.google.com/docs/database/android/start/#proguard
-keepattributes Signature
-keepclassmembers class org.dasfoo.delern.models.** {
  *;
}

# Keep ViewHolder classes because Firebase uses reflection on them:
# https://github.com/firebase/FirebaseUI-Android/issues/46#issuecomment-247516749
-keep class org.dasfoo.delern.viewholders.** {
  *;
}

# Keep all support lib classes
# http://stackoverflow.com/questions/22136032/action-bar-search-view-android-null-error
-keep class android.support.** {
  *;
}
-keep interface android.support.** {
  *;
}
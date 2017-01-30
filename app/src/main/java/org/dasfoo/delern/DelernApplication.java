package org.dasfoo.delern;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by katarina on 12/5/16.
 * Base class for maintaining global application state.
 */

public class DelernApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        /* Firebase apps automatically handle temporary network interruptions. Cached data will
        still be available while offline and your writes will be resent when network connectivity is
        recovered. Enabling disk persistence allows our app to also keep all of its state even after
        an app restart. */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

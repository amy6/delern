/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

import org.dasfoo.delern.models.Auth;
import org.dasfoo.delern.models.helpers.MultiWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric.sdk.android.Fabric;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by katarina on 12/5/16.
 * Base class for maintaining global application state.
 */

public class DelernApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelernApplication.class);

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

        Fabric.with(this, new Crashlytics());

        RxJavaPlugins.setErrorHandler(e -> LOGGER.error("Undeliverable RxJava error", e));

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        if (BuildConfig.DEBUG) {
            db.setLogLevel(com.google.firebase.database.Logger.Level.DEBUG);
        }
        /* Firebase apps automatically handle temporary network interruptions. Cached data will
        still be available while offline and your writes will be resent when network connectivity is
        recovered. Enabling disk persistence allows our app to also keep all of its state even after
        an app restart.
        https://firebase.google.com/docs/database/android/offline-capabilities */
        db.setPersistenceEnabled(true);
        MultiWrite.initializeOfflineListener(db);
        Auth.initializeCurrentUser(db);
    }
}

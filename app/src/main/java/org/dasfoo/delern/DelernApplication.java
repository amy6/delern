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
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;

import org.dasfoo.delern.models.User;
import org.dasfoo.delern.di.components.DaggerDelernMainActivityComponent;
import org.dasfoo.delern.di.components.DelernMainActivityComponent;
import org.dasfoo.delern.di.modules.DelernMainActivityModule;
import org.dasfoo.delern.views.IDelernMainView;

import io.fabric.sdk.android.Fabric;

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

        // Disable Crashlytics for instrumented builds (for CI).
        Crashlytics crashlyticsKit = new Crashlytics.Builder().core(
                new CrashlyticsCore.Builder().disabled(!BuildConfig.ENABLE_CRASHLYTICS).build())
                .build();
        Fabric.with(this, crashlyticsKit);

        User.initializeDatabase();
    }

    public static DelernMainActivityComponent getMainActivityInjector(IDelernMainView view) {
        return DaggerDelernMainActivityComponent
                .builder()
                .delernMainActivityModule(new DelernMainActivityModule(view)).build();
    }
}

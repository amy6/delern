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

package org.dasfoo.delern.util;

import android.net.Uri;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.dasfoo.delern.BuildConfig;
import org.dasfoo.delern.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Proxy for FirebaseRemoteConfig.
 */
public enum RemoteConfig {
    /**
     * The only instance of RemoteConfig, non-static to keep Context safe.
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfig.class);
    private static final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(2);

    private long mCacheExpirationSeconds = TimeUnit.HOURS.toSeconds(1);
    private final FirebaseRemoteConfig mFirebaseRemoteConfig;

    RemoteConfig() {
        // Get Remote Config instance.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        // We set default values in case of some parameters could not found in Remote Config Server.
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default_settigs);

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            mCacheExpirationSeconds = 0;
        }
    }

    /**
     * Check whether app needs to be updated or not. Method compares min remote version with
     * current version of app.
     *
     * @return whether app needs an update or not.
     */
    public boolean shouldForceUpdate() {
        long minAppVersion = mFirebaseRemoteConfig.getLong("min_app_version");
        long appVersion = BuildConfig.VERSION_CODE;
        return minAppVersion > appVersion;
    }

    /**
     * Check whether sharing is available.
     *
     * @return whether sharing feature available or not.
     */
    public boolean isSharingEnabled() {
        return mFirebaseRemoteConfig.getBoolean("sharing_feature_enabled");
    }

    /**
     * Get a link (Uri) to update the app from the Play Store.
     *
     * @return Uri to Play Store.
     */
    public Uri getUpdateUri() {
        return Uri.parse(mFirebaseRemoteConfig.getString("force_update_store_url"));
    }

    /**
     * Fetch remote config and report errors if any.
     *
     * @param onComplete called when remote config is fetched or the fetch failed.
     */
    public void fetch(final Runnable onComplete) {
        new TaskTimeoutWrapper<>(mFirebaseRemoteConfig.fetch(mCacheExpirationSeconds))
                .onCompleteOrTimeout(TIMEOUT_MILLIS, task -> {
                    // Task may be incomplete if end up here by timeout.
                    if (task.isComplete()) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            if (!(task.getException() instanceof
                                    FirebaseRemoteConfigFetchThrottledException)) {
                                // Throttling is okay; this may happen from time to time. Others
                                // must be logged and reported.
                                LOGGER.error("Failed to update RemoteConfig. Last fetch status: {}",
                                        mFirebaseRemoteConfig.getInfo().getLastFetchStatus(),
                                        task.getException());
                            }
                        }
                    }
                    // TODO(ksheremet): else { increase FB metric: remote_config_fetch_timeout }
                    onComplete.run();
                });
    }
}

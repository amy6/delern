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

package org.dasfoo.delern.remoteconfig;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.dasfoo.delern.util.LogUtil;

/**
 * Class checks whether app needs force update or not. It takes current version from
 * remote config and checks with version of application. It checks whether force update
 * is required as well.
 */
public class ForceUpdateChecker {

    private static final String TAG = LogUtil.tagFor(ForceUpdateChecker.class);

    private static final String MIN_APP_VERSION = "min_app_version";
    private static final String KEY_UPDATE_URL = "force_update_store_url";

    private final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    private final OnUpdateNeededListener mOnUpdateNeededListener;
    private final Context mContext;

    /**
     * Constructor. Initialize parameters.
     *
     * @param context context of Activity.
     * @param onUpdateNeededListener implementation if update is needed.
     */
    public ForceUpdateChecker(@NonNull final Context context, final
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.mContext = context;
        this.mOnUpdateNeededListener = onUpdateNeededListener;
    }

    /**
     * Check whether app needs to be updated or not. Method compares min remote version with
     * current version of app.
     *
     * @return whether app needs an update or not.
     */
    public boolean updateIsNeeded() {
        long minAppVersion = mFirebaseRemoteConfig.getLong(MIN_APP_VERSION);
        long appVersion = getAppVersion(mContext);
        return minAppVersion > appVersion;
    }

    /**
     * Call update method with Google Play link as parameter.
     */
    public void update() {
        String updateUrl = mFirebaseRemoteConfig.getString(KEY_UPDATE_URL);
        mOnUpdateNeededListener.onUpdateNeeded(updateUrl);
    }

    private long getAppVersion(final Context context) {
        long result = 0;
        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found:", e);
        }

        return result;
    }

    /**
     *
     */
    public interface OnUpdateNeededListener {
        /**
         * Handles if app needs to be updated.
         *
         * @param updateUrl url to Google Play's link of app.
         */
        void onUpdateNeeded(String updateUrl);
    }
}

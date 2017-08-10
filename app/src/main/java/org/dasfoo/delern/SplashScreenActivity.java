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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.dasfoo.delern.models.Auth;
import org.dasfoo.delern.signin.SignInActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splash Activity that check whether user needs force update of app or not.
 * If not, it starts DelernMainActivity.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplashScreenActivity.class);
    private static final long ONE_HOUR = 3600;
    private static final String KEY_MIN_APP_VERSION = "min_app_version";
    private static final String KEY_UPDATE_URL = "force_update_store_url";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    // In seconds.
    private long mCacheExpirationSeconds = ONE_HOUR;
    private OnCompleteListener<Void> mFetchRemoteConfigListener;

    /**
     * Starts SplashScreenActivity using context.
     *
     * @param context context from method that called this Activity.
     */
    public static void startActivity(final Context context) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mFetchRemoteConfigListener = task -> {
            if (task.isSuccessful()) {
                LOGGER.debug("remote config is fetched.");
                // After config data is successfully fetched, it must be activated
                // before newly fetched values are returned.
                mFirebaseRemoteConfig.activateFetched();
                if (updateIsNeeded()) {
                    update();
                    return;
                }
            } else {
                LOGGER.error("Remote config reading error", task.getException());
            }
            if (Auth.isSignedIn()) {
                DelernMainActivity.startActivity(this, Auth.getCurrentUser());
            } else {
                SignInActivity.startActivity(this);
            }
            finish();
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseRemoteConfig.fetch(mCacheExpirationSeconds)
                .addOnCompleteListener(mFetchRemoteConfigListener);
    }

    private void update() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.new_app_version_dialog_title)
                .setMessage(R.string.update_app_user_message)
                .setPositiveButton(R.string.update,
                        (dialogUpdate, which) ->
                                redirectForUpdate(mFirebaseRemoteConfig.getString(KEY_UPDATE_URL)))
                .setOnCancelListener(dialogCancel -> finish())
                .create();
        dialog.show();
    }

    private void redirectForUpdate(final String googlePlayUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Check whether app needs to be updated or not. Method compares min remote version with
     * current version of app.
     *
     * @return whether app needs an update or not.
     */
    private boolean updateIsNeeded() {
        long minAppVersion = mFirebaseRemoteConfig.getLong(KEY_MIN_APP_VERSION);
        long appVersion = BuildConfig.VERSION_CODE;
        return minAppVersion > appVersion;
    }
}

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.models.Auth;
import org.dasfoo.delern.signin.SignInActivity;
import org.dasfoo.delern.util.RemoteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splash Activity that check whether user needs force update of app or not.
 * If not, it starts DelernMainActivity.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplashScreenActivity.class);

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
    protected void onResume() {
        super.onResume();
        LOGGER.info("Fetching remote config");
        // TODO(ksheremet): add FB performance metric: splash_screen_duration
        RemoteConfig.INSTANCE.fetch(() -> {
            if (RemoteConfig.INSTANCE.shouldForceUpdate()) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.new_app_version_dialog_title)
                        .setMessage(R.string.update_app_user_message)
                        .setPositiveButton(R.string.update, (dialogUpdate, which) -> {
                            final Intent intent = new Intent(Intent.ACTION_VIEW,
                                    RemoteConfig.INSTANCE.getUpdateUri());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            // TODO(ksheremet): this crashes the app when nothing can handle Uri
                            startActivity(intent);
                        })
                        .setOnCancelListener(dialogCancel -> finish())
                        .create();
                dialog.show();
            } else {
                if (Auth.isSignedIn()) {
                    LOGGER.info("Redirecting to main activity");
                    DelernMainActivity.startActivity(this, Auth.getCurrentUser());
                } else {
                    LOGGER.info("Redirecting to singIn");
                    SignInActivity.startActivity(this);
                }
                finish();
            }
        });
    }
}

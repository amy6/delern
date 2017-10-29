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

package org.dasfoo.delern.notifications;

import android.os.Build;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.dasfoo.delern.models.Auth;
import org.dasfoo.delern.models.FCMToken;
import org.dasfoo.delern.models.User;

/**
 * Created by katarina on 10/7/16.
 */

public class DelernFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String DELERN_TOPIC = "delern_engage";

    /**
     * Saves token to the database, for our Cloud functions to be able to deliver notifications.
     */
    public static void saveCurrentToken() {
        User currentUser = Auth.getCurrentUser();
        // This code may run before user is authenticated; if that's the case, Auth will update the
        // token.
        if (currentUser.exists()) {
            // Save token to the database for external notifications.
            // NOTE: token must be kept private!
            FCMToken token = new FCMToken(currentUser);
            token.setName(Build.MANUFACTURER + " " + Build.MODEL);
            token.setKey(FirebaseInstanceId.getInstance().getToken());
            token.save();
        }
    }

    /**
     * The Application's current Instance ID token is no longer valid and thus a new one must be
     * requested.
     */
    @Override
    public void onTokenRefresh() {
        saveCurrentToken();

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(DELERN_TOPIC);
    }
}

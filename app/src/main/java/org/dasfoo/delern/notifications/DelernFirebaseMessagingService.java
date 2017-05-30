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

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.dasfoo.delern.util.LogUtil;

/**
 * Created by katarina on 10/7/16.
 * Class for communicating with Firebase Messaging.
 */

public class DelernFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(DelernFirebaseMessagingService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.i(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.i(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.i(TAG, "FCM Data Message: " + remoteMessage.getData());
    }
}

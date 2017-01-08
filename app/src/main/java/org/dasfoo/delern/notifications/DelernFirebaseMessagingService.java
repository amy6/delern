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

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }
}

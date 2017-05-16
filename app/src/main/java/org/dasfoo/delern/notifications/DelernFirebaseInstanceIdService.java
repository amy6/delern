package org.dasfoo.delern.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.dasfoo.delern.util.LogUtil;

/**
 * Created by katarina on 10/7/16.
 */

public class DelernFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(DelernFirebaseInstanceIdService.class);
    private static final String DELERN_TOPIC = "delern_engage";

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(DELERN_TOPIC);
    }
}

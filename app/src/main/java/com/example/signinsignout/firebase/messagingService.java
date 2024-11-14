package com.example.signinsignout.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service class to handle Firebase Cloud Messaging (FCM) events.
 */
public class messagingService extends FirebaseMessagingService {

    /**
     * Called when a new FCM registration token is generated.
     *
     * @param token The new registration token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
    }

    /**
     * Called when a new message is received from FCM.
     *
     * @param message The remote message received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM", "460 Message: " + message.getNotification().getBody());
    }
}

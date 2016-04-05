package app.gamd.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import app.gamd.MainActivity;
import app.gamd.R;
import app.gamd.common.Constantes;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class GcmBroadcastReceiver extends GcmListenerService {

    private static final String TAG = "GcmBroadcastReceiver";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        int requestCode = Integer.parseInt(data.getString("requestCode"));
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "RequestCode: " + requestCode);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(requestCode, message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(int requestCode, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constantes.NOTIFICATION_ID, requestCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence ticker ="Nueva entrada en Zona";
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.icon);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_sms)
                .setLargeIcon(icon)
                .setContentTitle("GAMD - Solicitud Aceptada")
                .setContentText(message)
                        //.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setLights(Color.RED, 1, 0)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{100, 250, 100, 500})
                .addAction(R.drawable.cast_ic_notification_1, ticker, pendingIntent)
                .setContentIntent(pendingIntent)
                .setTicker("Push: Mensajería");



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(requestCode, notificationBuilder.build());
    }
}

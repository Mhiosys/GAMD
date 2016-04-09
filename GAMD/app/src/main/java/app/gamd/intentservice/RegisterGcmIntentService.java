package app.gamd.intentservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import app.gamd.MainActivity;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.INotificacionService;
import app.gamd.model.NotificacionModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class RegisterGcmIntentService extends IntentService {

    private static final String TAG = "RegisterGcmIntentServ";
    private static final String[] TOPICS = {"global"};

    public RegisterGcmIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(Constantes.SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.d(TAG, "GCM Registration Token: " + token);
            String username = sharedPreferences.getString(Constantes.SETTING_USERNAME, "");

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(username, token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(Constantes.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Constantes.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constantes.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String username, String token) {
        // Add custom implementation, as needed.

        final NotificacionModel notificationModel = new NotificacionModel();
        notificationModel.setUsername(username);
        notificationModel.setCodigoGcm(token);
        //notificationModel.setCodigoDispositivo(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        INotificacionService notificacionService = ServiceGenerator.createService(INotificacionService.class);
        notificacionService.add(notificationModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                Log.d(TAG, jsonResponse.getMessage());
                if (jsonResponse.isSuccess()) {
                    //Guardamos los datos del registro
                    setRegistrationId(notificationModel.getUsername(), notificationModel.getCodigoGcm());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.ERROR_NO_CONTROLADO, Toast.LENGTH_LONG);
                toastMessage.show();
            }
        });
    }

    private void setRegistrationId(String user, String regId)
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);

        int appVersion = getAppVersion(getApplicationContext());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.USER, user);
        editor.putString(Constantes.REG_ID, regId);
        editor.putInt(Constantes.APP_VERSION, appVersion);
        editor.putLong(Constantes.EXPIRATION_TIME,
                System.currentTimeMillis() + Constantes.EXPIRATION_TIME_MS);

        editor.commit();
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}

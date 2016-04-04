package app.gamd.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.gamd.common.Constantes;

/**
 * Created by Sigcomt on 04/04/2016.
 */
public class GetAddressIntentService extends IntentService {

    private static final String TAG = "GetAddressIntentService";
    private static final String[] TOPICS = {"global"};

    public GetAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try
        {
            List<Address> addresses;
            Geocoder geocoder =
                    new Geocoder(getApplicationContext(), Locale.getDefault());
            final String latitud = sharedPreferences.getString(Constantes.LATITUD, "0");
            final String longitud = sharedPreferences.getString(Constantes.LONGITUD, "0");

            addresses = geocoder.getFromLocation(Double.parseDouble(latitud),Double.parseDouble(longitud), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constantes.DIRECCION, address);
            editor.commit();

        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }

        Intent registrationComplete = new Intent(Constantes.ADDRESS_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}

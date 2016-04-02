package app.gamd.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import app.gamd.common.enums.TipoInternet;

/**
 * Clase utilitaria que ayuda a saber el estado de la conexión a Internet del
 * dispositivo
 *
 * Created by Sigcomt on 31/03/2016.
 */
public class ConnectivityUtil {

    /**
     * Método que devuelve el tipo de conexión de internet que posee el
     * dispositivo
     *
     * @param context
     *            Contexto de la aplicación
     * @return Devuelve el tipo de conexión de internet que posee el dispositivo
     *         (WiFi, Movil, Ambos o Ninguno)
     */
    public static TipoInternet getInternetState(Context context) {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getTypeName().equalsIgnoreCase("wifi")) {
                    if (networkInfo.isConnected()) {
                        hasConnectedWifi = true;
                    }
                }
                if (networkInfo.getTypeName().equalsIgnoreCase("mobile")) {
                    if (networkInfo.isConnected()) {
                        hasConnectedMobile = true;
                    }
                }
            }

        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getTypeName().equalsIgnoreCase("wifi")) {
                            if (anInfo.isConnected()) {
                                hasConnectedWifi = true;
                            }
                        }
                        if (anInfo.getTypeName().equalsIgnoreCase("mobile")) {
                            if (anInfo.isConnected()) {
                                hasConnectedMobile = true;
                            }
                        }
                    }
                }
            }
        }

        if (hasConnectedWifi && hasConnectedMobile) {
            return TipoInternet.Ambos;
        } else if (hasConnectedWifi && !hasConnectedMobile) {
            return TipoInternet.WiFi;
        } else if (!hasConnectedWifi && hasConnectedMobile) {
            return TipoInternet.Movil;
        } else {
            return TipoInternet.Ninguno;
        }
    }

}


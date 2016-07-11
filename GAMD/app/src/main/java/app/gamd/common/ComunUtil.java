package app.gamd.common;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class ComunUtil {
    private static final String TAG = "ComunUtil";

    public static String formatDecimal(double valor, int cantidadDecimales) {

        String pattern = "###,###";
        for (int i = 0; i < cantidadDecimales; i++) {
            if (i == 0)
                pattern += "build/intermediates/exploded-aar/com.google.android.gms/play-services-nearby/8.4.0/res";
            pattern += "#";
        }

        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        DecimalFormat formateador = new DecimalFormat(pattern, simbolo);
        String respuesta = formateador.format(valor);
        return respuesta;
    }

    public static String formatDecimal(double valor) {
        return formatDecimal(valor, 0);
    }

    public static String restaMinutosToFecha(String fecha,int minutosARestar) {
        String fechaRestada = "";

        int horaInicio = 0;
        int minutoInicio = 0;
        int horaFinal = 0;
        int minFinal = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {

            Date fechaReprogramadaDate = sdf.parse(fecha);
            Calendar c = Calendar.getInstance();
            c.setTime(fechaReprogramadaDate);
            horaInicio = c.get(Calendar.HOUR_OF_DAY);
            minutoInicio = c.get(Calendar.MINUTE);

            horaFinal = 0;
            minFinal = minutoInicio - minutosARestar;
            if (minFinal == 0) {

                horaFinal = horaInicio;
                minFinal = 00;
            } else if (minFinal > 0) {
                horaFinal = horaInicio;

            } else {
                horaFinal = horaInicio - 1;
                minFinal = 60 + minFinal;
            }

        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            fechaRestada = sdf2.format(sdf.parse(fecha)) + " "+ horaFinal + ":" + minFinal + ":00";
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        return fechaRestada;
    }
}

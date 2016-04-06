package app.gamd.model;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class NotificacionModel {
    private String Username;
    private String CodigoGcm;
    private String Mensaje;
    private String CodigoDispositivo;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getCodigoGcm() {
        return CodigoGcm;
    }

    public void setCodigoGcm(String codigoGcm) {
        CodigoGcm = codigoGcm;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getCodigoDispositivo() {
        return CodigoDispositivo;
    }

    public void setCodigoDispositivo(String codigoDispositivo) {
        CodigoDispositivo = codigoDispositivo;
    }
}

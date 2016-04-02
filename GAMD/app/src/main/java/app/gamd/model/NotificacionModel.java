package app.gamd.model;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class NotificacionModel {
    private String Username;
    private String CodigoGCM;
    private String Mensaje;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getCodigoGCM() {
        return CodigoGCM;
    }

    public void setCodigoGCM(String codigoGCM) {
        CodigoGCM = codigoGCM;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }
}

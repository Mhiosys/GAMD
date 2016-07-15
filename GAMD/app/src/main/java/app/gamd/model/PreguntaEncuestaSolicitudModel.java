package app.gamd.model;

/**
 * Created by Sigcomt on 15/07/2016.
 */
public class PreguntaEncuestaSolicitudModel {
    private int PreguntaEncuestaId;
    private int SolicitudId;
    private int Calificacion;
    private String Observacion;

    public int getPreguntaEncuestaId() {
        return PreguntaEncuestaId;
    }

    public void setPreguntaEncuestaId(int preguntaEncuestaId) {
        PreguntaEncuestaId = preguntaEncuestaId;
    }

    public int getSolicitudId() {
        return SolicitudId;
    }

    public void setSolicitudId(int solicitudId) {
        SolicitudId = solicitudId;
    }

    public int getCalificacion() {
        return Calificacion;
    }

    public void setCalificacion(int calificacion) {
        Calificacion = calificacion;
    }

    public String getObservacion() {
        return Observacion;
    }

    public void setObservacion(String observacion) {
        Observacion = observacion;
    }
}

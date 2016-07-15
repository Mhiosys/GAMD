package app.gamd.model;

/**
 * Created by Sigcomt on 15/07/2016.
 */
public class PreguntaModel {
    private int EncuestaId;
    private int PreguntaId;
    private String Nombre;

    public int getEncuestaId() {
        return EncuestaId;
    }

    public void setEncuestaId(int encuestaId) {
        EncuestaId = encuestaId;
    }

    public int getPreguntaId() {
        return PreguntaId;
    }

    public void setPreguntaId(int preguntaId) {
        PreguntaId = preguntaId;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}

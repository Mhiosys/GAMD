package app.gamd.model;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public class SeekMedicalAttentionModel {
    private String ServicioId;
    private String Direccion;
    private String Sintomas;
    private double Latitud;
    private double Longitud;
    private int ClienteId;

    public String getServicioId() {
        return ServicioId;
    }

    public void setServicioId(String servicioId) {
        ServicioId = servicioId;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getSintomas() {
        return Sintomas;
    }

    public void setSintomas(String sintomas) {
        Sintomas = sintomas;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public int getClienteId() {
        return ClienteId;
    }

    public void setClienteId(int clienteId) {
        ClienteId = clienteId;
    }
}

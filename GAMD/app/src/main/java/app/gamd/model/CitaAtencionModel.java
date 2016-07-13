package app.gamd.model;

import java.util.Date;

/**
 * Created by Sigcomt on 13/07/2016.
 */
public class CitaAtencionModel {
    private int Id;
    private String NumSolicitud;
    private String Direccion;
    private String ServicioId;
    private Date FechaSolicitud;
    private int EstadoSolicitud;
    private Date FechaCita;
    private String HoraCita;
    private double Latitud;
    private double Longitud;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNumSolicitud() {
        return NumSolicitud;
    }

    public void setNumSolicitud(String numSolicitud) {
        NumSolicitud = numSolicitud;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getServicioId() {
        return ServicioId;
    }

    public void setServicioId(String servicioId) {
        ServicioId = servicioId;
    }

    public Date getFechaSolicitud() {
        return FechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        FechaSolicitud = fechaSolicitud;
    }

    public int getEstadoSolicitud() {
        return EstadoSolicitud;
    }

    public void setEstadoSolicitud(int estadoSolicitud) {
        EstadoSolicitud = estadoSolicitud;
    }

    public Date getFechaCita() {
        return FechaCita;
    }

    public void setFechaCita(Date fechaCita) {
        FechaCita = fechaCita;
    }

    public String getHoraCita() {
        return HoraCita;
    }

    public void setHoraCita(String horaCita) {
        HoraCita = horaCita;
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
}

package app.gamd.model;

/**
 * Created by Sigcomt on 27/07/2016.
 */
public class TipoModel {
    private String codigo;
    private String nombre;
    private String codigoParent;
    private String tipo;
    private int icono;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoParent() {
        return codigoParent;
    }

    public void setCodigoParent(String codigoParent) {
        this.codigoParent = codigoParent;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

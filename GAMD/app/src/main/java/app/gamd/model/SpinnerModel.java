package app.gamd.model;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public class SpinnerModel {
    private String codigo;
    private String nombre;
    private String codigoParent;
    private int icono;

    public SpinnerModel(String codigo, String nombre, int icono){
        super();
        this.codigo = codigo;
        this.nombre = nombre;
        this.icono = icono;
    }

    public SpinnerModel(String codigo, String nombre, int icono, String codigoParent){
        super();
        this.codigo = codigo;
        this.nombre = nombre;
        this.codigoParent = codigoParent;
        this.icono = icono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoParent() {
        return codigoParent;
    }

    public void setCodigoParent(String codigoParent) {
        this.codigoParent = codigoParent;
    }
}

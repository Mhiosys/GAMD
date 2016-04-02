package app.gamd.common;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public class SpinnerHolder {
    private ImageView icono;
    private TextView codigo;
    private TextView textView;

    public ImageView getIcono()
    {
        return icono;
    }

    public void setIcono(ImageView icono)
    {
        this.icono = icono;
    }

    public TextView getTextView()
    {
        return textView;
    }

    public void setTextView(TextView textView)
    {
        this.textView = textView;
    }

    public TextView getCodigo() {
        return codigo;
    }

    public void setCodigo(TextView codigo) {
        this.codigo = codigo;
    }
}

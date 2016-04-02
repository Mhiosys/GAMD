package app.gamd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.gamd.R;
import app.gamd.common.SpinnerHolder;
import app.gamd.model.SpinnerModel;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {
    private Context context;

    private List<SpinnerModel> spinnerModelList;

    public SpinnerAdapter(Context context, int resource, List<SpinnerModel> spinnerModelList)
    {
        super(context, resource, spinnerModelList);
        this.context = context;
        this.spinnerModelList = spinnerModelList;
    }

    //este método establece el elemento seleccionado sobre el botón del spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spinner_selected_item,null);
        }

        ((TextView) convertView.findViewById(R.id.codigo)).setText(spinnerModelList.get(position).getCodigo());
        ((TextView) convertView.findViewById(R.id.texto)).setText(spinnerModelList.get(position).getNombre());
        ((ImageView) convertView.findViewById(R.id.icono)).setBackgroundResource(spinnerModelList.get(position).getIcono());

        return convertView;
    }

    //gestiona la lista usando el View Holder Pattern. Equivale a la típica implementación del getView
    //de un Adapter de un ListView ordinario
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.spinner_list_item, parent, false);
        }

        if (row.getTag() == null)
        {
            SpinnerHolder spinnerHolder = new SpinnerHolder();
            spinnerHolder.setCodigo((TextView) row.findViewById(R.id.codigo));
            spinnerHolder.setIcono((ImageView) row.findViewById(R.id.icono));
            spinnerHolder.setTextView((TextView) row.findViewById(R.id.texto));
            row.setTag(spinnerHolder);
        }

        //rellenamos el layout con los datos de la fila que se está procesando
        SpinnerModel spinnerModel = spinnerModelList.get(position);
        ((SpinnerHolder) row.getTag()).getIcono().setImageResource(spinnerModel.getIcono());
        ((SpinnerHolder) row.getTag()).getTextView().setText(spinnerModel.getNombre());
        ((SpinnerHolder) row.getTag()).getCodigo().setText(spinnerModel.getCodigo());

        return row;
    }
}

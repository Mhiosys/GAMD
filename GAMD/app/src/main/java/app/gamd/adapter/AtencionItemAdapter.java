package app.gamd.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.gamd.R;
import app.gamd.holder.AtencionHolder;
import app.gamd.model.SeekMedicalAttentionModel;

/**
 * Created by Sigcomt on 13/07/2016.
 */
public class AtencionItemAdapter extends ArrayAdapter {
    private Activity context;
    private List<SeekMedicalAttentionModel> source;

    public AtencionItemAdapter(Activity context, int resource, List<SeekMedicalAttentionModel> source)
    {
        super(context, resource, source);
        this.context = context;
        this.source = source;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        AtencionHolder atencionHolder;

        if(item==null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_atencion, null);

            atencionHolder = new AtencionHolder();
            atencionHolder.direccion = (TextView)item.findViewById(R.id.lblDireccion);
            atencionHolder.fechaHoraAtencion = (TextView)item.findViewById(R.id.lblFecha);

            item.setTag(atencionHolder);
        }
        else
        {
            atencionHolder = (AtencionHolder) item.getTag();
        }

        SeekMedicalAttentionModel seekMedicalAttention = this.source.get(position);
        atencionHolder.direccion.setText(seekMedicalAttention.getDireccion());
        atencionHolder.fechaHoraAtencion.setText(seekMedicalAttention.getFechaHoraAtencion());

        return item;
    }
}

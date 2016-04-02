package app.gamd.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import app.gamd.R;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class ConsultaFragment extends Fragment  {

    TextView txtTitulo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_consulta, container, false);
        txtTitulo = (TextView) rootView.findViewById(R.id.txtTitulo);


        return rootView;
    }
}

package app.gamd.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.gamd.R;
import app.gamd.adapter.SpinnerAdapter;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.IEncuestaService;
import app.gamd.model.PreguntaModel;
import app.gamd.model.SpecialistModel;
import app.gamd.model.SpinnerModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sigcomt on 15/07/2016.
 */
public class FinalizarEncuestaDialogFragment extends DialogFragment {

    private OnRegistrarListener mCallback;
    private int solicitudId;
    private TextView txtPregunta1Id, txtPregunta1, txtPregunta2Id, txtPregunta2, txtPregunta3Id, txtPregunta3;
    private Spinner spPregunta1, spPregunta2, spPregunta3;
    private EditText txtObservacion;
    private Button btnAceptar;
    private static final String TAG = "FinalizarEncuestaDialogFragment";
    ProgressDialog progress;
    private View viewFinalizarEncuestaDialogFragment;

    public interface OnRegistrarListener {
        void onRegistrar(int solicitudId, int pregunta1Id, int pregunta2Id, int pregunta3Id, int respuesta1, int respuesta2, int respuesta3, String observaciones );
    }

    public FinalizarEncuestaDialogFragment() {
    }

    public FinalizarEncuestaDialogFragment(OnRegistrarListener handler) {
        this.mCallback = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewFinalizarEncuestaDialogFragment = inflater.inflate(R.layout.alert_encuesta_finish, container, false);

        solicitudId = getArguments().getInt("solicitudId");

        txtPregunta1Id = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta1Id);
        txtPregunta2Id = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta2Id);
        txtPregunta3Id = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta3Id);

        txtPregunta1 = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta1);
        txtPregunta2 = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta2);
        txtPregunta3 = (TextView) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtPregunta3);

        txtObservacion = (EditText) viewFinalizarEncuestaDialogFragment.findViewById(R.id.txtObservacion);
        btnAceptar = (Button) viewFinalizarEncuestaDialogFragment.findViewById(R.id.btnAceptarFinalizar);

        spPregunta1 = (Spinner) viewFinalizarEncuestaDialogFragment.findViewById(R.id.spPregunta1);
        spPregunta2 = (Spinner) viewFinalizarEncuestaDialogFragment.findViewById(R.id.spPregunta2);
        spPregunta3 = (Spinner) viewFinalizarEncuestaDialogFragment.findViewById(R.id.spPregunta3);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validarEnvio())
                {
                    mCallback.onRegistrar(solicitudId, Integer.parseInt(txtPregunta1Id.getText().toString()), Integer.parseInt(txtPregunta2Id.getText().toString()), Integer.parseInt(txtPregunta3Id.getText().toString()), Integer.parseInt(((SpinnerModel)spPregunta1.getSelectedItem()).getCodigo()), Integer.parseInt(((SpinnerModel)spPregunta2.getSelectedItem()).getCodigo()), Integer.parseInt(((SpinnerModel)spPregunta3.getSelectedItem()).getCodigo()), txtObservacion.getText().toString());
                    dismiss();
                }else{
                    Snackbar.make(viewFinalizarEncuestaDialogFragment, Constantes.INGRESE_TODOS_LOS_DATOS, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        List<SpinnerModel> items = new ArrayList<SpinnerModel>(6);
        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
        items.add(new SpinnerModel("1", getString(R.string.Uno), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("2", getString(R.string.Dos), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("3", getString(R.string.Tres), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("4", getString(R.string.Cuatro), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("5", getString(R.string.Cinco), R.drawable.ic_action_accept));

        spPregunta1.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));
        spPregunta2.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));
        spPregunta3.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));

        GetEncuestas();

        return viewFinalizarEncuestaDialogFragment;
    }

    private boolean validarEnvio(){
        boolean result = true;
        String direccion = this.txtObservacion.getText().toString();
        if(direccion.equals("")
                || spPregunta1.getSelectedItemPosition()==0
                || spPregunta2.getSelectedItemPosition()==0
                || spPregunta3.getSelectedItemPosition()==0){
            result = false;
        }

        return result;
    }

    private void GetEncuestas()
    {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        IEncuestaService encuestaService = ServiceGenerator.createService(IEncuestaService.class);
        encuestaService.getPreguntas(new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    ArrayList preguntaItemModels = (ArrayList) jsonResponse.getData();
                    int contador = 1;

                    for (Object item : preguntaItemModels) {
                        Map mapper = (Map) item;
                        if(contador == 1)
                        {
                            txtPregunta1Id.setText(String.valueOf((int) Double.parseDouble(mapper.get("Id").toString())));
                            txtPregunta1.setText(mapper.get("Nombre").toString());
                        }

                        if(contador == 2)
                        {
                            txtPregunta2Id.setText(String.valueOf((int) Double.parseDouble(mapper.get("Id").toString())));
                            txtPregunta2.setText(mapper.get("Nombre").toString());
                        }

                        if(contador == 3)
                        {
                            txtPregunta3Id.setText(String.valueOf((int) Double.parseDouble(mapper.get("Id").toString())));
                            txtPregunta3.setText(mapper.get("Nombre").toString());
                        }

                        contador = contador +1;
                    }
                }else{
                    Snackbar.make(viewFinalizarEncuestaDialogFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar.make(viewFinalizarEncuestaDialogFragment, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

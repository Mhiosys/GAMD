package app.gamd.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.gamd.MainActivity;
import app.gamd.R;
import app.gamd.adapter.AtencionItemAdapter;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.ISeekMedicalAttentionService;
import app.gamd.model.SeekMedicalAttentionModel;
import app.gamd.model.SpecialistModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sigcomt on 12/07/2016.
 */
public class AppointmentAttentionFragment extends Fragment {

    ListView lvAtencionPendiente, lvAtencionEnCurso;
    ProgressDialog progress;
    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    private static final String TAG = "AppointmentAttention";
    private View viewAppointmentAttentionFragment;
    private Toolbar toolbar;

    public AppointmentAttentionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        viewAppointmentAttentionFragment = inflater.inflate(R.layout.fragment_appointment_attention, container, false);
        lvAtencionPendiente = (ListView)viewAppointmentAttentionFragment.findViewById(R.id.lvAtencionPendiente);
        lvAtencionEnCurso = (ListView)viewAppointmentAttentionFragment.findViewById(R.id.lvAtencionEnCurso);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);

        getSolicitudesPendientes();


        return viewAppointmentAttentionFragment;
    }

    private void getSolicitudesPendientes()
    {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        final int especialistaId = sharedPreferences.getInt(Constantes.SETTING_USUARIOID, 0);
        SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
        seekMedicalAttentionModel.setEspecialistaId(especialistaId);
        ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
        seekMedicalAttentionService.getSolicitudesPendientes(seekMedicalAttentionModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                //progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    Log.d(TAG, jsonResponse.getData().toString());

                    ArrayList seekMedicalAttentionItemModels = (ArrayList) jsonResponse.getData();
                    List<SeekMedicalAttentionModel> seekMedicalAttentionItemModelListJson = new ArrayList<SeekMedicalAttentionModel>();

                    for (Object item : seekMedicalAttentionItemModels) {
                        Map mapper = (Map) item;
                        SeekMedicalAttentionModel seekMedicalAttentionItemModelFila = new SeekMedicalAttentionModel();
                        seekMedicalAttentionItemModelFila.setSolicitudId((int) Double.parseDouble(mapper.get("Id").toString()));
                        seekMedicalAttentionItemModelFila.setClienteId((int) Double.parseDouble(mapper.get("ClienteId").toString()));
                        seekMedicalAttentionItemModelFila.setDireccion(mapper.get("Direccion").toString());
                        seekMedicalAttentionItemModelFila.setFechaAtencion(mapper.get("FechaCita").toString().substring(0,10));
                        seekMedicalAttentionItemModelFila.setHoraAtencion(mapper.get("HoraCita").toString());
                        seekMedicalAttentionItemModelListJson.add(seekMedicalAttentionItemModelFila);
                    }

                    AtencionItemAdapter titularItemAdapter = new AtencionItemAdapter(getActivity(), R.layout.list_item_atencion, seekMedicalAttentionItemModelListJson);
                    lvAtencionPendiente.setAdapter(titularItemAdapter);
                    getSolicitudesEnCurso();
                    lvAtencionPendiente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d(TAG, String.valueOf(position));
                            int solicitudId = ((SeekMedicalAttentionModel) parent.getAdapter().getItem(position)).getSolicitudId();
                            showDialogConfirmar(solicitudId);
                        }
                    });

                } else {

                    Snackbar.make(viewAppointmentAttentionFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showDialogConfirmar(int numeroSolicitud){
        final int solicitudId = numeroSolicitud;
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Confirmar_Llegada)
                .setMessage(R.string.Esta_seguro_llegada)
                .setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmarLlegadaCita(solicitudId);
                    }
                })
                .setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private void getSolicitudesEnCurso()
    {
        /*
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();
        */
        final int especialistaId = sharedPreferences.getInt(Constantes.SETTING_USUARIOID, 0);
        SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
        seekMedicalAttentionModel.setEspecialistaId(especialistaId);
        ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
        seekMedicalAttentionService.getSolicitudesActivas(seekMedicalAttentionModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    Log.d(TAG, jsonResponse.getData().toString());

                    ArrayList seekMedicalAttentionItemModels = (ArrayList) jsonResponse.getData();
                    List<SeekMedicalAttentionModel> seekMedicalAttentionItemModelListJson = new ArrayList<SeekMedicalAttentionModel>();

                    for (Object item : seekMedicalAttentionItemModels) {
                        Map mapper = (Map) item;
                        SeekMedicalAttentionModel seekMedicalAttentionItemModelFila = new SeekMedicalAttentionModel();
                        seekMedicalAttentionItemModelFila.setSolicitudId((int) Double.parseDouble(mapper.get("Id").toString()));
                        seekMedicalAttentionItemModelFila.setClienteId((int) Double.parseDouble(mapper.get("ClienteId").toString()));
                        seekMedicalAttentionItemModelFila.setDireccion(mapper.get("Direccion").toString());
                        seekMedicalAttentionItemModelFila.setFechaAtencion(mapper.get("FechaCita").toString().substring(0,10));
                        seekMedicalAttentionItemModelFila.setHoraAtencion(mapper.get("HoraCita").toString());
                        seekMedicalAttentionItemModelListJson.add(seekMedicalAttentionItemModelFila);
                    }

                    AtencionItemAdapter titularItemAdapter = new AtencionItemAdapter(getActivity(), R.layout.list_item_atencion, seekMedicalAttentionItemModelListJson);
                    lvAtencionEnCurso.setAdapter(titularItemAdapter);

                    lvAtencionEnCurso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d(TAG, String.valueOf(position));
                            int solicitudId = ((SeekMedicalAttentionModel) parent.getAdapter().getItem(position)).getSolicitudId();
                            showDialogFinalizar(solicitudId);
                        }
                    });

                } else {

                    Snackbar.make(viewAppointmentAttentionFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showDialogFinalizar(int numeroSolicitud){
        final int solicitudId = numeroSolicitud;
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Finalizar_Servicio)
                .setMessage(R.string.Esta_seguro_finalizar)
                .setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizarCita(solicitudId);
                    }
                })
                .setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private void confirmarLlegadaCita(int numeroSolicitud){
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
        seekMedicalAttentionModel.setSolicitudId(numeroSolicitud);
        ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
        seekMedicalAttentionService.confirmarLlegadaCita(seekMedicalAttentionModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    getSolicitudesPendientes();
                    //getSolicitudesEnCurso();
                }else{
                    Snackbar.make(viewAppointmentAttentionFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar.make(viewAppointmentAttentionFragment, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void finalizarCita(int numeroSolicitud){
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
        seekMedicalAttentionModel.setSolicitudId(numeroSolicitud);
        seekMedicalAttentionModel.setObservacion("Todo OK");
        ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
        seekMedicalAttentionService.finalizarCita(seekMedicalAttentionModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    getSolicitudesPendientes();
                    //getSolicitudesEnCurso();
                }else{
                    Snackbar.make(viewAppointmentAttentionFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar.make(viewAppointmentAttentionFragment, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Object object);
    }
}

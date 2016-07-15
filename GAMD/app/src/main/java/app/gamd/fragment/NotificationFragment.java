package app.gamd.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.gamd.R;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.IEncuestaService;
import app.gamd.contract.ISeekMedicalAttentionService;
import app.gamd.dialogfragment.FinalizarEncuestaDialogFragment;
import app.gamd.model.PreguntaEncuestaSolicitudModel;
import app.gamd.model.PreguntaModel;
import app.gamd.model.SeekMedicalAttentionModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NotificationFragment extends Fragment implements FinalizarEncuestaDialogFragment.OnRegistrarListener{

    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    TextView lblMensaje;
    ProgressDialog progress;
    private View viewNotificationFragment;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewNotificationFragment = inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
        int solicitudId = sharedPreferences.getInt(Constantes.NOTIFICATION_ID, 0);
        String mensaje = sharedPreferences.getString(Constantes.NOTIFICATION_MESSAGE, "");
        String tipo = sharedPreferences.getString(Constantes.NOTIFICATION_TYPE, "");
        lblMensaje = (TextView)viewNotificationFragment.findViewById(R.id.lblMensaje);
        lblMensaje.setText(mensaje);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constantes.NOTIFICATION_TYPE, 0);
        editor.putString(Constantes.NOTIFICATION_MESSAGE, "");
        editor.putString(Constantes.NOTIFICATION_TYPE, "");
        editor.commit();

        if(tipo.equals(Constantes.NOTIFICATION_TYPE_FINALIZACION))
        {
            FinalizarEncuestaDialogFragment dialog = new FinalizarEncuestaDialogFragment(NotificationFragment.this);
            Bundle bundle = new Bundle();
            bundle.putInt("solicitudId", solicitudId);
            dialog.setArguments(bundle);
            dialog.setCancelable(false);
            dialog.show( getActivity().getFragmentManager(), "dialog");
        }

        return viewNotificationFragment;
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

    @Override
    public void onRegistrar(int solicitudId, int pregunta1Id, int pregunta2Id, int pregunta3Id, int respuesta1, int respuesta2, int respuesta3, String observaciones) {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        ArrayList<PreguntaEncuestaSolicitudModel> preguntaEncuestaSolicitudModelList = new ArrayList<PreguntaEncuestaSolicitudModel>();
        PreguntaEncuestaSolicitudModel preguntaEncuestaSolicitudModel = new PreguntaEncuestaSolicitudModel();

        preguntaEncuestaSolicitudModel.setSolicitudId(solicitudId);
        preguntaEncuestaSolicitudModel.setPreguntaEncuestaId(pregunta1Id);
        preguntaEncuestaSolicitudModel.setCalificacion(respuesta1);
        preguntaEncuestaSolicitudModel.setObservacion(observaciones);
        preguntaEncuestaSolicitudModelList.add(preguntaEncuestaSolicitudModel);

        preguntaEncuestaSolicitudModel.setSolicitudId(solicitudId);
        preguntaEncuestaSolicitudModel.setPreguntaEncuestaId(pregunta2Id);
        preguntaEncuestaSolicitudModel.setCalificacion(respuesta2);
        preguntaEncuestaSolicitudModel.setObservacion(observaciones);
        preguntaEncuestaSolicitudModelList.add(preguntaEncuestaSolicitudModel);

        preguntaEncuestaSolicitudModel.setSolicitudId(solicitudId);
        preguntaEncuestaSolicitudModel.setPreguntaEncuestaId(pregunta3Id);
        preguntaEncuestaSolicitudModel.setCalificacion(respuesta3);
        preguntaEncuestaSolicitudModel.setObservacion(observaciones);
        preguntaEncuestaSolicitudModelList.add(preguntaEncuestaSolicitudModel);

        IEncuestaService encuestaService = ServiceGenerator.createService(IEncuestaService.class);
        encuestaService.registrarEncuesta(preguntaEncuestaSolicitudModelList, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (!jsonResponse.isSuccess()) {
                    Snackbar.make(viewNotificationFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar.make(viewNotificationFragment, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Object object);
    }
}

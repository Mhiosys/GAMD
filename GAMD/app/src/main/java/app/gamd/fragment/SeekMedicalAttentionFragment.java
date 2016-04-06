package app.gamd.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.gamd.MainActivity;
import app.gamd.R;
import app.gamd.adapter.SpinnerAdapter;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.ISeekMedicalAttentionService;
import app.gamd.model.SeekMedicalAttentionModel;
import app.gamd.model.SpecialistModel;
import app.gamd.model.SpinnerModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeekMedicalAttentionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SeekMedicalAttentionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Spinner spTipoServicio, spEspecialidad, spServicio;
    Button btnSolicitar;
    EditText txtDireccionS, txtSintomas;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;
    private static final String TAG = "SeekMedicalAttention";
    private View viewSeekMedicalFragment;
    private Toolbar toolbar;

    public SeekMedicalAttentionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewSeekMedicalFragment = inflater.inflate(R.layout.fragment_seek_medical_attention, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);

        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        txtDireccionS = (EditText)viewSeekMedicalFragment.findViewById(R.id.txtDireccionS);
        txtSintomas = (EditText)viewSeekMedicalFragment.findViewById(R.id.txtSintomas);
        btnSolicitar = (Button)viewSeekMedicalFragment.findViewById(R.id.btnSolicitar);
        spTipoServicio = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spTipoServicio);
        spEspecialidad = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spEspecialidad);
        spServicio = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spServicio);
        spEspecialidad.setEnabled(false);
        spServicio.setEnabled(false);

        txtDireccionS.setText(sharedPreferences.getString(Constantes.DIRECCION, ""));
        List<SpinnerModel> items = new ArrayList<SpinnerModel>(3);
        items.add(new SpinnerModel("0",getString(R.string.Ninguno), R.drawable.ic_action_cancel));
        items.add(new SpinnerModel("1",getString(R.string.Tipo_Servicio_1), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("2", getString(R.string.Tipo_Servicio_2), R.drawable.ic_action_accept));
        spTipoServicio.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));

        spTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                List<SpinnerModel> items = new ArrayList<SpinnerModel>(1);
                items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                String codigo = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

                switch (Integer.parseInt(codigo)) {
                    case 0:
                        spEspecialidad.setEnabled(false);
                        break;
                    case 1:
                        items = new ArrayList<SpinnerModel>(4);
                        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                        items.add(new SpinnerModel("1", getString(R.string.Especialidad_1), R.drawable.ic_action_accept));
                        items.add(new SpinnerModel("4", getString(R.string.Especialidad_4), R.drawable.ic_action_accept));
                        items.add(new SpinnerModel("5", getString(R.string.Especialidad_5), R.drawable.ic_action_accept));
                        spEspecialidad.setEnabled(true);
                        break;
                    case 2:
                        items = new ArrayList<SpinnerModel>(2);
                        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                        items.add(new SpinnerModel("2", getString(R.string.Especialidad_2), R.drawable.ic_action_accept));
                        spEspecialidad.setEnabled(true);
                        break;
                    case 3:
                        items = new ArrayList<SpinnerModel>(2);
                        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                        items.add(new SpinnerModel("3", getString(R.string.Especialidad_3), R.drawable.ic_action_accept));
                        spEspecialidad.setEnabled(true);
                        break;
                    default:
                        spEspecialidad.setEnabled(false);
                        break;
                }
                spEspecialidad.setAdapter(new SpinnerAdapter(getActivity(), R.layout.spinner_selected_item, items));

                spEspecialidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        List<SpinnerModel> items = new ArrayList<SpinnerModel>(1);
                        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                        String codigo = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

                        switch (Integer.parseInt(codigo)) {
                            case 0:
                                spServicio.setEnabled(false);
                                break;
                            case 1:
                                items = new ArrayList<SpinnerModel>(3);
                                items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                                items.add(new SpinnerModel("1", getString(R.string.Servicio_1), R.drawable.ic_action_accept));
                                items.add(new SpinnerModel("2", getString(R.string.Servicio_2), R.drawable.ic_action_accept));
                                spServicio.setEnabled(true);
                                break;
                            case 2:
                                items = new ArrayList<SpinnerModel>(2);
                                items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                                items.add(new SpinnerModel("3", getString(R.string.Servicio_3), R.drawable.ic_action_accept));
                                spServicio.setEnabled(true);
                                break;
                            case 3:
                                items = new ArrayList<SpinnerModel>(2);
                                items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                                items.add(new SpinnerModel("4", getString(R.string.Servicio_4), R.drawable.ic_action_accept));
                                spServicio.setEnabled(true);
                                break;
                            default:
                                spServicio.setEnabled(false);
                                break;
                        }
                        spServicio.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        //nothing
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarEnvio()) {
                    progress = new ProgressDialog(getActivity());
                    progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
                    progress.show();

                    final String direccion = txtDireccionS.getText().toString();
                    final String sintomas = txtSintomas.getText().toString();
                    final String servicioId = ((SpinnerModel) spServicio.getSelectedItem()).getCodigo();
                    final String latitud = sharedPreferences.getString(Constantes.LATITUD, "0");
                    final String longitud = sharedPreferences.getString(Constantes.LONGITUD, "0");
                    final int clienteId = sharedPreferences.getInt(Constantes.SETTING_USUARIOID, 0);
                    final String cliente = sharedPreferences.getString(Constantes.SETTING_USERNAME, "");

                    SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
                    seekMedicalAttentionModel.setDireccion(direccion);
                    seekMedicalAttentionModel.setSintomas(sintomas);
                    seekMedicalAttentionModel.setServicioId(servicioId);
                    seekMedicalAttentionModel.setClienteId(clienteId);
                    seekMedicalAttentionModel.setClienteUserName(cliente);
                    seekMedicalAttentionModel.setLatitud(Double.parseDouble(latitud));
                    seekMedicalAttentionModel.setLongitud(Double.parseDouble(longitud));

                    ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
                    seekMedicalAttentionService.crearSolicitud(seekMedicalAttentionModel, new Callback<JsonResponse>() {
                        @Override
                        public void success(JsonResponse jsonResponse, Response response) {
                            progress.dismiss();
                            if (jsonResponse.isSuccess()) {
                                Log.d(TAG, jsonResponse.getData().toString());

                                ArrayList specialistItemModels = (ArrayList) jsonResponse.getData();
                                List<SpecialistModel> specialistItemModelListJson = new ArrayList<SpecialistModel>();

                                for (Object item : specialistItemModels) {
                                    Map mapper = (Map) item;
                                    SpecialistModel specialistItemModelFila = new SpecialistModel();
                                    specialistItemModelFila.setId((int)Double.parseDouble(mapper.get("Id").toString()));
                                    specialistItemModelFila.setDni("11111111");
                                    //specialistItemModelFila.setDni(mapper.get("Dni").toString());
                                    specialistItemModelFila.setNombre(mapper.get("Nombre").toString());
                                    specialistItemModelFila.setApellido(mapper.get("Apellido").toString());
                                    specialistItemModelFila.setDireccion(mapper.get("Direccion").toString());
                                    specialistItemModelFila.setLatitud(Double.parseDouble(mapper.get("Latitud").toString()));
                                    specialistItemModelFila.setLongitud(Double.parseDouble(mapper.get("Longitud").toString()));
                                    specialistItemModelListJson.add(specialistItemModelFila);
                                }

                                ((MainActivity)getActivity()).specialistItemModelListJson = specialistItemModelListJson;

                                Fragment fragment = new MapFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.linearLayoutMain, fragment)
                                        .commit();
                                toolbar.setTitle(R.string.title_activity_main);

                                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                navigationView.getMenu().getItem(0).setChecked(true);

                            } else {
                                Snackbar.make(viewSeekMedicalFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress.dismiss();
                            Snackbar.make(viewSeekMedicalFragment, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });

                } else {
                    Snackbar.make(viewSeekMedicalFragment, Constantes.INGRESE_TODOS_LOS_DATOS, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return viewSeekMedicalFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private boolean validarEnvio(){
        boolean result = true;
        String direccion = this.txtDireccionS.getText().toString();
        String sintomas = this.txtSintomas.getText().toString();
        if(direccion.equals("") || sintomas.equals("")
                || spTipoServicio.getSelectedItemPosition()==0
                || spEspecialidad.getSelectedItemPosition()==0
                || spServicio.getSelectedItemPosition()==0){
            result = false;
        }
        return result;
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
        void onFragmentInteraction(Uri uri);
    }
}

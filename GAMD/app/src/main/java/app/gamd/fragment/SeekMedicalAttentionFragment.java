package app.gamd.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gamd.MainActivity;
import app.gamd.R;
import app.gamd.adapter.PlacesAutoCompleteAdapter;
import app.gamd.adapter.SpinnerAdapter;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.ISeekMedicalAttentionService;
import app.gamd.contract.ITipoService;
import app.gamd.dialogfragment.CustomDatePickerFragmentDialog;
import app.gamd.model.CitaAtencionModel;
import app.gamd.model.SeekMedicalAttentionModel;
import app.gamd.model.SpecialistModel;
import app.gamd.model.SpinnerModel;
import app.gamd.model.TipoModel;
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
public class SeekMedicalAttentionFragment extends Fragment
        implements CustomDatePickerFragmentDialog.OnDateSelectedListener,  GoogleApiClient.OnConnectionFailedListener {

    private OnFragmentInteractionListener mListener;
    Spinner spHoraServicio, spTipoServicio, spEspecialidad, spServicio;
    Button btnSolicitar;
    EditText txtSintomas, txtFechaAtencion;
    ProgressDialog progress;
    private SimpleDateFormat dateFormatter;
    SharedPreferences sharedPreferences;
    private static final String TAG = "SeekMedicalAttention";
    private View viewSeekMedicalFragment;
    private Toolbar toolbar;
    private int year;
    private int month;
    private int day;
    private List<TipoModel> tipoList;
    private List<SpinnerModel> tipoServicioList;
    private List<SpinnerModel> especialistaList;
    private List<SpinnerModel> servicioList;

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    private PlacesAutoCompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private TextView mPlaceDetailsAttribution;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-12.092732, -79.028154), new LatLng(-12.092732, -77.048314));

    public SeekMedicalAttentionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewSeekMedicalFragment = inflater.inflate(R.layout.fragment_seek_medical_attention, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        tipoServicioList = new ArrayList<SpinnerModel>();
        especialistaList = new ArrayList<SpinnerModel>();
        servicioList = new ArrayList<SpinnerModel>();

        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        txtFechaAtencion = (EditText) viewSeekMedicalFragment.findViewById(R.id.txtFechaAtencion);
        txtSintomas = (EditText)viewSeekMedicalFragment.findViewById(R.id.txtSintomas);
        btnSolicitar = (Button)viewSeekMedicalFragment.findViewById(R.id.btnSolicitar);
        spHoraServicio = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spHoraServicio);
        spTipoServicio = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spTipoServicio);
        spEspecialidad = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spEspecialidad);
        spServicio = (Spinner)viewSeekMedicalFragment.findViewById(R.id.spServicio);
        spEspecialidad.setEnabled(false);
        spServicio.setEnabled(false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                viewSeekMedicalFragment.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsAttribution = (TextView) viewSeekMedicalFragment.findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlacesAutoCompleteAdapter(getActivity().getApplicationContext(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        mAutocompleteView.setText(sharedPreferences.getString(Constantes.DIRECCION, ""));
        txtFechaAtencion.setInputType(InputType.TYPE_NULL);
        List<SpinnerModel> items = new ArrayList<SpinnerModel>(14);
        items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
        items.add(new SpinnerModel("1", getString(R.string.Horario_1), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("2", getString(R.string.Horario_2), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("3", getString(R.string.Horario_3), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("4", getString(R.string.Horario_4), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("5", getString(R.string.Horario_5), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("6", getString(R.string.Horario_6), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("7", getString(R.string.Horario_7), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("8", getString(R.string.Horario_8), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("9", getString(R.string.Horario_9), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("10", getString(R.string.Horario_10), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("11", getString(R.string.Horario_11), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("12", getString(R.string.Horario_12), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("13", getString(R.string.Horario_13), R.drawable.ic_action_accept));
        spHoraServicio.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));

        spHoraServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String codigo = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

                if(codigo.equals(Constantes.ENESTEMOMENTO)){
                    Snackbar.make(viewSeekMedicalFragment, Constantes.MENSAJEENESTEMOMENTO, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

        CargarTipo();

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarEnvio()) {
                    progress = new ProgressDialog(getActivity());
                    progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
                    progress.show();

                    final String direccion = mAutocompleteView.getText().toString();
                    final String sintomas = txtSintomas.getText().toString();
                    final String fechaAtencion = txtFechaAtencion.getText().toString();
                    final String horaAtencion = ((SpinnerModel) spHoraServicio.getSelectedItem()).getNombre();
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
                    seekMedicalAttentionModel.setFechaAtencion(fechaAtencion);
                    seekMedicalAttentionModel.setHoraAtencion(horaAtencion);

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
                                    specialistItemModelFila.setId((int) Double.parseDouble(mapper.get("Id").toString()));
                                    specialistItemModelFila.setDni("11111111");
                                    //specialistItemModelFila.setDni(mapper.get("Dni").toString());
                                    specialistItemModelFila.setNombre(mapper.get("Nombre").toString());
                                    specialistItemModelFila.setApellido(mapper.get("Apellido").toString());
                                    specialistItemModelFila.setDireccion(mapper.get("Direccion").toString());
                                    specialistItemModelFila.setLatitud(Double.parseDouble(mapper.get("Latitud").toString()));
                                    specialistItemModelFila.setLongitud(Double.parseDouble(mapper.get("Longitud").toString()));
                                    specialistItemModelListJson.add(specialistItemModelFila);
                                }

                                ((MainActivity) getActivity()).specialistItemModelListJson = specialistItemModelListJson;

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constantes.TIENEPOINT, Constantes.SITIENEPOINT);
                                editor.commit();

                                Fragment fragment = new MapFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.linearLayoutMain, fragment)
                                        .addToBackStack(null)
                                        .commit();
                                toolbar.setTitle(R.string.title_activity_main);

                                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                navigationView.getMenu().getItem(0).setChecked(true);

                            } else {

                                if(jsonResponse.getData()!= null)
                                {
                                    LinkedTreeMap citaAtencionModel = (LinkedTreeMap) jsonResponse.getData();
                                    String mensajePendiente = jsonResponse.getMessage();
                                    Integer solicitudId = (int) Double.parseDouble(citaAtencionModel.get("Id").toString());
                                    Integer numeroSolicitud = Integer.parseInt(citaAtencionModel.get("NumSolicitud").toString());
                                    mensajePendiente += ("\r\nSolicitud Nro: " + numeroSolicitud);
                                    mensajePendiente += ("\r\nPendiente de atención");
                                    mensajePendiente += ("\r\nProgramada para " + citaAtencionModel.get("FechaCita").toString().substring(0,10));
                                    mensajePendiente += ("\r\nRango: " + citaAtencionModel.get("HoraCita").toString());

                                    showDialog(mensajePendiente, solicitudId);
                                }else{
                                    Snackbar.make(viewSeekMedicalFragment, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
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

        Calendar newCalendar = Calendar.getInstance();
        year = newCalendar.get(Calendar.YEAR);
        month = newCalendar.get(Calendar.MONTH);
        day = newCalendar.get(Calendar.DAY_OF_MONTH);
        onDateSelected(year, month+1, day);

        txtFechaAtencion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CustomDatePickerFragmentDialog dialog = new CustomDatePickerFragmentDialog();
                dialog.setHandler(SeekMedicalAttentionFragment.this);
                dialog.setDate(year, month-1, day);
                dialog.show(getActivity().getFragmentManager(), "dialog");
            }
        });

        return viewSeekMedicalFragment;
    }

    private void CargarTipo()
    {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();
        ITipoService tipoService = ServiceGenerator.createService(ITipoService.class);
        tipoService.getTipos(new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    Log.d(TAG, jsonResponse.getData().toString());
                    tipoList = (ArrayList) jsonResponse.getData();

                    for (Object item : tipoList) {
                        Map mapper = (Map) item;
                        if(mapper.get("Tipo").toString().equals("1")){
                            if(mapper.get("Codigo").toString().equals("0")){
                                tipoServicioList.add(new SpinnerModel(mapper.get("Codigo").toString(),  mapper.get("Nombre").toString(), R.drawable.ic_action_cancel,  mapper.get("CodigoParent").toString()));
                            }else{
                                tipoServicioList.add(new SpinnerModel(mapper.get("Codigo").toString(),  mapper.get("Nombre").toString(), R.drawable.ic_action_accept,  mapper.get("CodigoParent").toString()));
                            }
                        }

                        if(mapper.get("Tipo").toString().equals("2")){
                            if(mapper.get("Codigo").toString().equals("0")){
                                especialistaList.add(new SpinnerModel(mapper.get("Codigo").toString(),  mapper.get("Nombre").toString(), R.drawable.ic_action_cancel,  mapper.get("CodigoParent").toString()));
                            }else{
                                especialistaList.add(new SpinnerModel(mapper.get("Codigo").toString(),  mapper.get("Nombre").toString(), R.drawable.ic_action_accept,  mapper.get("CodigoParent").toString()));
                            }
                        }

                        if(mapper.get("Tipo").toString().equals("3")){
                            if(mapper.get("Codigo").toString().equals("0")){
                                servicioList.add(new SpinnerModel(mapper.get("Codigo").toString(),  mapper.get("Nombre").toString(), R.drawable.ic_action_cancel,  mapper.get("CodigoParent").toString()));
                            }else{
                                servicioList.add(new SpinnerModel(mapper.get("Codigo").toString(), mapper.get("Nombre").toString(), R.drawable.ic_action_accept,  mapper.get("CodigoParent").toString()));
                            }
                        }

                    }
                    spTipoServicio.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, tipoServicioList));

                    spTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            List<SpinnerModel> items = new ArrayList<SpinnerModel>();
                            items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                            String codigo = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

                            for (SpinnerModel item : especialistaList) {
                                if(item.getCodigoParent().equals(codigo))
                                {
                                    items.add(new SpinnerModel(item.getCodigo(),item.getNombre(),R.drawable.ic_action_accept, item.getCodigoParent()));
                                }
                            }

                            spEspecialidad.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_selected_item, items));

                            spEspecialidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                    List<SpinnerModel> items = new ArrayList<SpinnerModel>();
                                    items.add(new SpinnerModel("0", getString(R.string.Ninguno), R.drawable.ic_action_cancel));
                                    String codigo = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

                                    for (SpinnerModel item : servicioList) {
                                        if(item.getCodigoParent().equals(codigo))
                                        {
                                            items.add(new SpinnerModel(item.getCodigo(),item.getNombre(),R.drawable.ic_action_accept, item.getCodigoParent()));
                                        }
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

                }else {
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
    }

    private void showDialog(String mensaje, int numeroSolicitud){
        final int solicitudId = numeroSolicitud;
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Solicitud_Pendiente)
                .setMessage(mensaje)
                .setPositiveButton(R.string.Cancelar_Cita, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialogEleccion(solicitudId);
                    }
                })
                .setNegativeButton(R.string.Cerrar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private void showDialogEleccion(int numeroSolicitud){
        final int solicitudId = numeroSolicitud;
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Confirmación)
                .setMessage(R.string.Esta_seguro_confirmacion)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelarCita(solicitudId);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void cancelarCita(int numeroSolicitud){
        progress = new ProgressDialog(getActivity());
        progress.setMessage(Constantes.ENVIANDO_SOLICITUD);
        progress.show();

        SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
        seekMedicalAttentionModel.setSolicitudId(numeroSolicitud);
        ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
        seekMedicalAttentionService.cancelarCita(seekMedicalAttentionModel, new Callback<JsonResponse>() {
            @Override
            public void success(JsonResponse jsonResponse, Response response) {
                progress.dismiss();
                if (jsonResponse.isSuccess()) {
                    Fragment fragment = new MapFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.linearLayoutMain, fragment)
                            .addToBackStack(null)
                            .commit();
                    toolbar.setTitle(R.string.title_activity_main);

                    NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }else{
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

    }

    private boolean validarEnvio(){
        boolean result = true;
        Date fechaAtencionDate = null;
        String direccion = this.mAutocompleteView.getText().toString();
        String sintomas = this.txtSintomas.getText().toString();
        String fechaAtencion = txtFechaAtencion.getText().toString();
        if(direccion.equals("") || sintomas.equals("")
                || fechaAtencion.equals("")
                || spHoraServicio.getSelectedItemPosition()==0
                || spTipoServicio.getSelectedItemPosition()==0
                || spEspecialidad.getSelectedItemPosition()==0
                || spServicio.getSelectedItemPosition()==0){
            result = false;
        }

        try {
            fechaAtencionDate = dateFormatter.parse(fechaAtencion);
        } catch (ParseException ex) {
            Log.d(TAG, ex.getMessage());
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

    @Override
    public void onDateSelected(int yearSelected, int monthSelected, int daySelected) {
        year = yearSelected;
        month = monthSelected;
        day = daySelected;
        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
        txtFechaAtencion.setText(fecha);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getActivity().getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constantes.LATITUD, Double.toString(place.getLatLng().latitude));
            editor.putString(Constantes.LONGITUD, Double.toString(place.getLatLng().longitude));
            editor.putString(Constantes.DIRECCION, place.getAddress().toString());
            editor.putString(Constantes.TIENEPOINT, Constantes.SITIENEPOINT);
            editor.commit();

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity().getApplicationContext(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
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

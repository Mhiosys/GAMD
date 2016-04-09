package app.gamd.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import app.gamd.MainActivity;
import app.gamd.R;
import app.gamd.common.Constantes;
import app.gamd.common.PermissionUtils;
import app.gamd.intentservice.GetAddressIntentService;
import app.gamd.model.SpecialistModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap googleMap;
    private OnFragmentInteractionListener mListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MapFragment";
    private boolean ubicado = false;
    private LatLng myLocation;
    SharedPreferences sharedPreferences;
    private static final String MAP_FRAGMENT_TAG = "mapView";
    private BroadcastReceiver mGetAddressBroadcastReceiver;
    private boolean isReceiverRegistered;

    private Toolbar toolbar;
    private Button btnSolicitar;
    private TextView txtDireccion;
    private ProgressBar pbBusqueda;
    ProgressDialog progress;
    private View viewMapFragment;
    LocationManager locationManager;
    AlertDialog alert = null;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewMapFragment = inflater.inflate(R.layout.fragment_map, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);

        btnSolicitar = (Button)viewMapFragment.findViewById(R.id.btnSolicitar);
        txtDireccion = (TextView)viewMapFragment.findViewById(R.id.txtDireccion);
        pbBusqueda = (ProgressBar) viewMapFragment.findViewById(R.id.pbBusqueda);
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);

        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        /****Mejora****/
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(Constantes.DESEA_ACTIVAR_GPS)
                    .setCancelable(false)
                    .setPositiveButton(Constantes.SI, new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(Constantes.NO, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();

        }
        /********/

        mGetAddressBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pbBusqueda.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        getActivity().getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
                String sentDireccion = sharedPreferences
                        .getString(Constantes.DIRECCION, "");
                txtDireccion.setText(sentDireccion);
            }
        };
        registerReceiver();

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SeekMedicalAttentionFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.linearLayoutMain, fragment)
                        .commit();
                toolbar.setTitle(R.string.title_activity_seek_medical_attention);

                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        });

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        // Then we add it using a FragmentTransaction.
        FragmentTransaction fragmentTransaction =
                getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutMapa, mapFragment, MAP_FRAGMENT_TAG);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        return viewMapFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGetAddressBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGetAddressBroadcastReceiver,
                    new IntentFilter(Constantes.ADDRESS_COMPLETE));
            isReceiverRegistered = true;
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
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;

        // We will provide our own zoom controls.
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.0696208, -77.0379043), 10));

        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if(!ubicado)
            {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constantes.LATITUD, Double.toString(location.getLatitude()));
                editor.putString(Constantes.LONGITUD, Double.toString(location.getLongitude()));
                editor.commit();
                sendFindAddressToIntentService();

                if(googleMap != null){

                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Mi Ubicación")
                            .snippet("Usuario")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_locate)));

                    if(((MainActivity)getActivity()).specialistItemModelListJson!=null){

                        addMarkersToMap(((MainActivity)getActivity()).specialistItemModelListJson);
                        if (myLocation != null) {
                            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(myLocation)
                                    .zoom(13.5f)
                                    .bearing(0)
                                    .tilt(50)
                                    .build()));
                        }

                        String mensaje = "";
                        if(((MainActivity)getActivity()).specialistItemModelListJson.size()>0){
                            mensaje = Constantes.SE_ENCONTRARON_MEDICOS;
                        }else{
                            mensaje = Constantes.NO_SE_ENCONTRARON_MEDICOS;
                        }

                        Snackbar.make(viewMapFragment, mensaje, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        ((MainActivity)getActivity()).specialistItemModelListJson = null;
                    }else{
                        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(myLocation)
                                .zoom(16.5f)
                                .bearing(0)
                                .tilt(50)
                                .build()));
                    }
                    ubicado=true;
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the location layer. Request the location permission if needed.
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void addMarkersToMap(List<SpecialistModel> specialistModels){
        for (SpecialistModel item : specialistModels) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitud(), item.getLongitud()))
                    .title(item.getNombre() + " " + item.getApellido())
                    .snippet(item.getDireccion())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)));
        }
    }

    private void changeCamera(CameraUpdate update) {
        if (!checkReady()) {
            return;
        }
        changeCamera(update, null);
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {

        if (!checkReady()) {
            return;
        }

        googleMap.animateCamera(update, callback);
    }

    private void sendFindAddressToIntentService() {
        // Inicia IntentService la búsqueda de dirección
        Intent intent = new Intent(getActivity(), GetAddressIntentService.class);
        getActivity().startService(intent);
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (googleMap == null) {
            Snackbar.make(viewMapFragment, R.string.map_not_ready, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }
        return true;
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
        void onFragmentInteraction(Uri uri);
    }
}

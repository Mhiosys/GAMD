package app.gamd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.common.PermissionUtils;
import app.gamd.contract.ISpecialistService;
import app.gamd.model.SpecialistModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Button btnSolicitar;
    private TextView txtDireccion;
    private ProgressBar pbBusqueda;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    SharedPreferences sharedPreferences;
    ProgressDialog progress;
    private static final String TAG = "MainActivity";
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mShowPermissionDeniedDialog = false;
    private boolean ubicado = false;
    private LatLng myLocation;

    private static final LatLng HospitalA = new LatLng(-12.095287, -77.037201);

    private static final LatLng FarmaciaA = new LatLng(-12.098686, -77.042952);

    private static final LatLng ConsultorioA = new LatLng(-12.096378, -77.053552);

    private static final LatLng ConsultorioB = new LatLng(-12.048273, -77.049582);

    private static final LatLng FarmaciaB = new LatLng(-12.041674, -77.039690);

    private static final LatLng HospitalB = new LatLng(-12.041789, -77.048370);

    private static final LatLng ConsultorioC = new LatLng(-11.998105, -77.071448);

    private static final LatLng HospitalC = new LatLng(-12.004737, -77.093743);

    private static final LatLng FarmaciaC = new LatLng(-11.958825, -77.106201);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        toolbar.setLogo(R.drawable.icon);
        setSupportActionBar(toolbar);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
        btnSolicitar = (Button)findViewById(R.id.btnSolicitar);
        txtDireccion = (TextView)findViewById(R.id.txtDireccion);
        pbBusqueda = (ProgressBar) findViewById(R.id.pbBusqueda);

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSeek = new Intent(MainActivity.this, SeekMedicalAttentionActivity.class);
                startActivity(intentSeek);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Médicos más cercanos a 2 KM", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                progress = new ProgressDialog(MainActivity.this);
                progress.setMessage("Buscando médicos cercanos ...");
                progress.show();

                final String latitud = sharedPreferences.getString(Constantes.LATITUD, "0");
                final String longitud = sharedPreferences.getString(Constantes.LONGITUD, "0");

                SpecialistModel specialistModel = new SpecialistModel();
                specialistModel.setLatitud(Double.parseDouble(latitud));
                specialistModel.setLongitud(Double.parseDouble(longitud));

                ISpecialistService specialistService = ServiceGenerator.createService(ISpecialistService.class);
                specialistService.getEspecialistas(specialistModel, new Callback<JsonResponse>() {
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
                                specialistItemModelFila.setId(Integer.parseInt(mapper.get("Id").toString()));
                                specialistItemModelFila.setDni(mapper.get("Dni").toString());
                                specialistItemModelFila.setNombre(mapper.get("Nombre").toString());
                                specialistItemModelFila.setApellido(mapper.get("Apellido").toString());
                                specialistItemModelFila.setDireccion(mapper.get("Direccion").toString());
                                specialistItemModelFila.setLatitud(Double.parseDouble(mapper.get("Latitud").toString()));
                                specialistItemModelFila.setLongitud(Double.parseDouble(mapper.get("Longitud").toString()));
                                specialistItemModelListJson.add(specialistItemModelFila);
                            }

                            mMap.clear();

                            mMap.addMarker(new MarkerOptions()
                                    .position(myLocation)
                                    .title("Mi Ubicación")
                                    .snippet("Usuario")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_locate)));

                            addMarkersToMap(specialistItemModelListJson);
                            if (myLocation != null) {
                                changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(myLocation)
                                        .zoom(13.5f)
                                        .bearing(0)
                                        .tilt(50)
                                        .build()));
                            }

                            String mensaje = "";
                            if(specialistItemModels.size()>0){
                                mensaje = "Se encontraron los siguientes Médicos más cercanos a 2 KM";
                            }else{
                                mensaje = "No hay médicos disponibles cercanos a 2 KM";
                            }
                            Toast toastMessage = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG);
                            toastMessage.show();
                        } else {
                            Toast toastMessage = Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG);
                            toastMessage.show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.ERROR_NO_CONTROLADO, Toast.LENGTH_LONG);
                        toastMessage.show();
                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addMarkersToMap(List<SpecialistModel> specialistModels){
        for (SpecialistModel item : specialistModels) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitud(), item.getLongitud()))
                    .title(item.getNombre() + " " + item.getApellido())
                    .snippet(item.getDireccion())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)));
        }
    }

    private void addMarkersToMap() {

        // Uses a colored icon.
        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Mi Ubicación")
                .snippet("Belcorp")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_locate)));

        // Uses a colored icon.
        mMap.addMarker(new MarkerOptions()
            .position(HospitalA)
            .title("HospitalA")
            .snippet("San Isidro: 2,074,200")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        mMap.addMarker(new MarkerOptions()
            .position(FarmaciaA)
                .title("FarmaciaA")
                .snippet("San Isidro: 4,627,300")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .infoWindowAnchor(0.5f, 0.5f));

        // Creates a draggable marker. Long press to drag.
        mMap.addMarker(new MarkerOptions()
            .position(ConsultorioA)
            .title("ConsultorioA")
            .snippet("San Isidro: 4,137,400")
            .draggable(true));

        // A few more markers for good measure.
        mMap.addMarker(new MarkerOptions()
                .position(ConsultorioB)
                .title("Cercado")
                .snippet("Population: 1,738,800"));

        mMap.addMarker(new MarkerOptions()
                .position(FarmaciaB)
                .title("FarmaciaB")
                .snippet("Cercado: 1,213,000"));

        mMap.addMarker(new MarkerOptions()
                .position(HospitalB)
                .title("HospitalB")
                .snippet("Cercado: 1,213,000"));

        mMap.addMarker(new MarkerOptions()
                .position(ConsultorioC)
                .title("ConsultorioC")
                .snippet("San Martín: 4,233,000"));

        mMap.addMarker(new MarkerOptions()
                .position(HospitalC)
                .title("HospitalC")
                .snippet("San Martín: 3,293,000"));

        mMap.addMarker(new MarkerOptions()
                .position(FarmaciaC)
                .title("FarmaciaC")
                .snippet("San Martín: 5,123,000"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateEnabledState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // We will provide our own zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.0696208, -77.0379043), 10));

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
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

                mMap.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .title("Mi Ubicación")
                        .snippet("Usuario")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_locate)));

                if(mMap != null){

                    changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(myLocation)
                            .zoom(16.5f)
                            .bearing(0)
                            .tilt(50)
                            .build()), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            pbBusqueda.setVisibility(ProgressBar.GONE);
                            //Toast.makeText(getBaseContext(), "Ubicación completada", Toast.LENGTH_SHORT).show();

                            Geocoder geocoder;
                            List<Address> addresses;

                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(myLocation.latitude, myLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                txtDireccion.setText(address);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constantes.DIRECCION, address);
                                editor.commit();
                            } catch (IOException e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onCancel() {
                            pbBusqueda.setVisibility(ProgressBar.GONE);
                            Toast.makeText(getBaseContext(), "Ubicación cancelada", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                ubicado=true;
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, false);
            }
        } else {
            mShowPermissionDeniedDialog = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowPermissionDeniedDialog) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mShowPermissionDeniedDialog = false;
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

        mMap.animateCamera(update, callback);
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

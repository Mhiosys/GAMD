package app.gamd;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.gamd.common.Constantes;
import app.gamd.fragment.MapFragment;
import app.gamd.fragment.NotificationFragment;
import app.gamd.fragment.SeekMedicalAttentionFragment;
import app.gamd.intentservice.RegisterGcmIntentService;
import app.gamd.model.SpecialistModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MapFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        SeekMedicalAttentionFragment.OnFragmentInteractionListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Toolbar toolbar;
    private static final String TAG = "MainActivity";
    public List<SpecialistModel> specialistItemModelListJson;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token;
    SharedPreferences sharedPreferences;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);;
        String username = sharedPreferences.getString(Constantes.SETTING_USERNAME, null);
        String telefono = sharedPreferences.getString(Constantes.SETTING_TELEFONO, null);

        if(username==null) {
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        toolbar.setLogo(R.drawable.icon);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.txtUsuarioMenu)).setText(username);
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.txtTelefonoMenu)).setText(telefono);
        navigationView.getMenu().getItem(0).setChecked(true);

        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.linearLayoutMain, fragment)
                .commit();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Constantes.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(getApplicationContext(), getString(R.string.gcm_send_message), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.token_error_message), Toast.LENGTH_LONG).show();
                }
            }
        };

        // Registrando BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            //Obtenemos el Registration ID guardado
            token = getRegistrationId(getApplicationContext());
            if(token.equals("")){
                // Inicia IntentService el registro de esta aplicación en GCM.
                Intent intent = new Intent(this, RegisterGcmIntentService.class);
                startService(intent);
            }
        }
    }

    private String getRegistrationId(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(Constantes.REG_ID, "");

        if (registrationId.length() == 0)
        {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(Constantes.USER, "user");

        int registeredVersion =
                prefs.getInt(Constantes.APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(Constantes.EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        }
        else if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        }

        return registrationId;
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
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

        boolean fragmentTransaction = false;
        Fragment fragment = null;

        if (id == R.id.nav_map) {
            fragment = new MapFragment();
            fragmentTransaction = true;
        } else if (id == R.id.nav_seek) {
            fragment = new SeekMedicalAttentionFragment();
            fragmentTransaction = true;
        } else if (id == R.id.nav_notification) {
            fragment = new NotificationFragment();
            fragmentTransaction = true;
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = Constantes.SHARED_MY_APPLICATION;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constantes.SUBJECT_SEND);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }  else if (id == R.id.nav_about) {
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_logout) {

            sharedPreferences.edit().remove(Constantes.SETTING_USERNAME).commit();
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            finish();

        }

        if(fragmentTransaction){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.linearLayoutMain, fragment)
                    .commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constantes.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Comprueba el dispositivo para asegurarse de que tiene el servicios de Google Play APK. Si
     * no es así, mostrar un cuadro de diálogo que permite a los usuarios descargar el APK desde
     * Google Play Store o activar en la configuración del sistema del dispositivo.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "Este dispositivo no es soportado");
                finish();
            }
            return false;
        }
        return true;
    }
}

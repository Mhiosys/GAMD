package app.gamd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.gamd.adapter.SpinnerAdapter;
import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.ISeekMedicalAttentionService;
import app.gamd.contract.IUsuarioService;
import app.gamd.model.SeekMedicalAttentionModel;
import app.gamd.model.SpinnerModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SeekMedicalAttentionActivity extends AppCompatActivity {

    Spinner spTipoServicio, spEspecialidad, spServicio;
    Button btnSolicitar;
    EditText txtDireccion, txtSintomas;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_medical_attention);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_seek_medical_attention);
        toolbar.setLogo(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);

        txtDireccion = (EditText)findViewById(R.id.txtDireccion);
        txtSintomas = (EditText)findViewById(R.id.txtSintomas);
        btnSolicitar = (Button)findViewById(R.id.btnSolicitar);
        spTipoServicio = (Spinner)findViewById(R.id.spTipoServicio);
        spEspecialidad = (Spinner)findViewById(R.id.spEspecialidad);
        spServicio = (Spinner)findViewById(R.id.spServicio);
        spEspecialidad.setEnabled(false);
        spServicio.setEnabled(false);

        txtDireccion.setText(sharedPreferences.getString(Constantes.DIRECCION, ""));
        List<SpinnerModel> items = new ArrayList<SpinnerModel>(3);
        items.add(new SpinnerModel("0",getString(R.string.Ninguno), R.drawable.ic_action_cancel));
        items.add(new SpinnerModel("1",getString(R.string.Tipo_Servicio_1), R.drawable.ic_action_accept));
        items.add(new SpinnerModel("2",getString(R.string.Tipo_Servicio_2), R.drawable.ic_action_accept));
        spTipoServicio.setAdapter(new SpinnerAdapter(this, R.layout.spinner_selected_item, items));
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
                spEspecialidad.setAdapter(new SpinnerAdapter(getApplicationContext(), R.layout.spinner_selected_item, items));

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
                        spServicio.setAdapter(new SpinnerAdapter(getApplicationContext(), R.layout.spinner_selected_item, items));
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
                    progress = new ProgressDialog(SeekMedicalAttentionActivity.this);
                    progress.setMessage("Enviando Solicitud ...");
                    progress.show();

                    final String direccion = txtDireccion.getText().toString();
                    final String sintomas = txtSintomas.getText().toString();
                    final String servicioId = ((SpinnerModel) spServicio.getSelectedItem()).getCodigo();
                    final String latitud = sharedPreferences.getString(Constantes.LATITUD, "0");
                    final String longitud = sharedPreferences.getString(Constantes.LONGITUD, "0");

                    SeekMedicalAttentionModel seekMedicalAttentionModel = new SeekMedicalAttentionModel();
                    seekMedicalAttentionModel.setDireccion(direccion);
                    seekMedicalAttentionModel.setSintomas(sintomas);
                    seekMedicalAttentionModel.setServicioId(servicioId);
                    seekMedicalAttentionModel.setClienteId(1);
                    seekMedicalAttentionModel.setLatitud(Double.parseDouble(latitud));
                    seekMedicalAttentionModel.setLongitud(Double.parseDouble(longitud));

                    ISeekMedicalAttentionService seekMedicalAttentionService = ServiceGenerator.createService(ISeekMedicalAttentionService.class);
                    seekMedicalAttentionService.crearSolicitud(seekMedicalAttentionModel, new Callback<JsonResponse>() {
                        @Override
                        public void success(JsonResponse jsonResponse, Response response) {
                            progress.dismiss();
                            if (jsonResponse.isSuccess()) {
                                Log.d(TAG, jsonResponse.getData().toString());
                                Toast toastMessage = Toast.makeText(getApplicationContext(), jsonResponse.getData().toString(), Toast.LENGTH_LONG);
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

                } else {
                    Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.INGRESE_TODOS_LOS_DATOS, Toast.LENGTH_SHORT);
                    toastMessage.show();
                }
            }
        });
    }

    private boolean validarEnvio(){
        boolean result = true;
        String direccion = this.txtDireccion.getText().toString();
        String sintomas = this.txtSintomas.getText().toString();
        if(direccion.equals("") || sintomas.equals("")
                || spTipoServicio.getSelectedItemPosition()==0
                || spEspecialidad.getSelectedItemPosition()==0
                || spServicio.getSelectedItemPosition()==0){
            result = false;
        }
        return result;
    }
}

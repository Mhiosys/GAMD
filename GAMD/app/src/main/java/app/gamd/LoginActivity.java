package app.gamd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.IUsuarioService;
import app.gamd.model.UsuarioModel;
import app.gamd.service.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sigcomt on 30/03/2016.
 */
public class LoginActivity extends AppCompatActivity {

    Button btnEntrar, btnCrear;
    EditText txtUsuario;
    EditText txtClave;
    TextView txtOlvidoClave;
    SharedPreferences sharedPreferences;
    ProgressDialog progress;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constantes.PREFERENCES, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Constantes.SETTING_USERNAME, null);
        if(username!=null) {
            Intent intentLogeado = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentLogeado);
            finish();
        }
        else{
            btnEntrar = (Button) findViewById(R.id.btnEntrar);
            btnCrear = (Button) findViewById(R.id.btnCrear);
            txtUsuario = (EditText) findViewById(R.id.txtUsuario);
            txtClave = (EditText) findViewById(R.id.txtClave);
            txtOlvidoClave = (TextView)findViewById(R.id.txtOlvidoClave);

            txtOlvidoClave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentRecoverPassword = new Intent(LoginActivity.this, RecoverPasswordActivity.class);
                    startActivity(intentRecoverPassword);
                }
            });

            btnCrear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentRegisterUser = new Intent(LoginActivity.this, RegisterUserActivity.class);
                    startActivity(intentRegisterUser);
                }
            });

            btnEntrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validarEnvio()) {
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setMessage("Iniciando Sesión ...");
                        progress.show();

                        final String username = txtUsuario.getText().toString();
                        final String clave = txtClave.getText().toString();
                        UsuarioModel usuarioModel = new UsuarioModel();
                        usuarioModel.setUsername(username);
                        usuarioModel.setClave(clave);

                        IUsuarioService usuarioService = ServiceGenerator.createService(IUsuarioService.class);
                        usuarioService.getLogin(usuarioModel, new Callback<JsonResponse>() {
                            @Override
                            public void success(JsonResponse jsonResponse, Response response) {
                                progress.dismiss();
                                if (jsonResponse.isSuccess()) {
                                    Map mapper = (Map) jsonResponse.getData();
                                    if(mapper.get("Estado").toString().equals("1")){
                                        //Guarda los valores en las preferencias compartidas
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(Constantes.SETTING_USUARIOID, mapper.get("UsuarioAsociadoId").toString());
                                        editor.putString(Constantes.SETTING_USERNAME, mapper.get("Username").toString());
                                        editor.putString(Constantes.SETTING_CLAVE, mapper.get("Clave").toString());
                                        editor.putString(Constantes.SETTING_NOMBRECOMPLETO, mapper.get("NombreCompleto").toString());
                                        editor.putString(Constantes.SETTING_CONTINUAR, "1");
                                        editor.commit();

                                        Intent intentMenu = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intentMenu);
                                        finish();
                                    }else{
                                        Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.USUARIO_INACTIVO, Toast.LENGTH_LONG);
                                        toastMessage.show();
                                    }

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
                        Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.INGRESE_USERNAME_CLAVE, Toast.LENGTH_SHORT);
                        toastMessage.show();
                    }
                }
            });
        }
    }

    private boolean validarEnvio(){
        boolean result = true;
        String username = this.txtUsuario.getText().toString();
        String clave = this.txtClave.getText().toString();
        if(username.equals("") || clave.equals("")){
            result = false;
        }
        return result;
    }
}

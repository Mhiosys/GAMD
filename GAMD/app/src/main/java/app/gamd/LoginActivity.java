package app.gamd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import app.gamd.common.Constantes;
import app.gamd.common.JsonResponse;
import app.gamd.contract.IClienteService;
import app.gamd.model.LoginModel;
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
                    final View viewLogin = v;
                    if (validarEnvio()) {
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setMessage("Iniciando Sesión ...");
                        progress.show();

                        final String username = txtUsuario.getText().toString();
                        final String clave = txtClave.getText().toString();
                        LoginModel loginModel = new LoginModel();
                        loginModel.setUsername(username);
                        loginModel.setPassword(clave);

                        IClienteService clienteService = ServiceGenerator.createService(IClienteService.class);
                        clienteService.getGetUserCliente(loginModel, new Callback<JsonResponse>() {
                            @Override
                            public void success(JsonResponse jsonResponse, Response response) {
                                progress.dismiss();
                                if (jsonResponse.isSuccess()) {
                                    Map mapper = (Map) jsonResponse.getData();
                                    if (mapper.get("Estado").toString().equals(Constantes.SETTING_ACTIVO)) {
                                        //Guarda los valores en las preferencias compartidas
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt(Constantes.SETTING_USUARIOID, (int)Double.parseDouble(mapper.get("Id").toString()));
                                        editor.putString(Constantes.SETTING_USERNAME, mapper.get("Username").toString());
                                        editor.putString(Constantes.SETTING_NOMBRE, mapper.get("Nombre").toString());
                                        editor.putString(Constantes.SETTING_APELLIDO, mapper.get("Apellido").toString());
                                        editor.putString(Constantes.SETTING_CELULAR, mapper.get("Celular").toString());
                                        editor.putString(Constantes.SETTING_CONTINUAR, "1");
                                        editor.commit();

                                        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intentMain);
                                        finish();
                                    } else {
                                        Snackbar.make(viewLogin, Constantes.USUARIO_INACTIVO, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }

                                } else {
                                    Snackbar.make(viewLogin, jsonResponse.getMessage(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progress.dismiss();
                                Snackbar.make(viewLogin, Constantes.ERROR_NO_CONTROLADO, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    } else {
                        Snackbar.make(viewLogin,  Constantes.INGRESE_USERNAME_CLAVE, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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

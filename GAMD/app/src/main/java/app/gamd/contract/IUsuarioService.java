package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.LoginModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 12/07/2016.
 */
public interface IUsuarioService {
    @POST("/api/Usuario/GetUsuario")
    void getUsuario(@Body LoginModel loginModel, Callback<JsonResponse> cb);
}

package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.LoginModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public interface IClienteService {
    @POST("/api/Cliente/GetUserCliente")
    void getGetUserCliente(@Body LoginModel loginModel, Callback<JsonResponse> cb);
}

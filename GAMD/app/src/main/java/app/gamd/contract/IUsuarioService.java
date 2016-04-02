package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.UsuarioModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public interface IUsuarioService {
    @POST("/api/UsuarioAsociado/GetLogin")
    void getLogin(@Body UsuarioModel usuarioModel, Callback<JsonResponse> cb);

    @POST("/api/UsuarioAsociado/GetLogOut")
    void getLogOut(@Body UsuarioModel usuarioModel, Callback<JsonResponse> cb);
}

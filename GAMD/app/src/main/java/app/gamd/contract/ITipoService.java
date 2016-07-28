package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.TipoModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 27/07/2016.
 */
public interface ITipoService {

    @POST("/api/Tipo/GetTipos")
    void getTipos(Callback<JsonResponse> cb);
}

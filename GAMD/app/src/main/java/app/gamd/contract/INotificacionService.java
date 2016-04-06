package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.NotificacionModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public interface INotificacionService {
    @POST("/api/Notificacion/Add")
    void add(@Body NotificacionModel notificacionModel, Callback<JsonResponse> cb);
}

package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.SeekMedicalAttentionModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public interface ISeekMedicalAttentionService {
    @POST("/api/SolicitudAtencion/CrearSolicitud")
    void crearSolicitud(@Body SeekMedicalAttentionModel seekMedicalAttentionModel, Callback<JsonResponse> cb);

    @POST("/api/SolicitudAtencion/CancelarCita")
    void cancelarCita(@Body SeekMedicalAttentionModel seekMedicalAttentionModel, Callback<JsonResponse> cb);
}

package app.gamd.contract;

import java.util.ArrayList;

import app.gamd.common.JsonResponse;
import app.gamd.model.PreguntaEncuestaSolicitudModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 15/07/2016.
 */
public interface IEncuestaService {
    @POST("/api/Encuesta/GetEncuestas")
    void getEncuestas(Callback<JsonResponse> cb);

    @POST("/api/Encuesta/GetPreguntas")
    void getPreguntas(Callback<JsonResponse> cb);

    @POST("/api/Encuesta/RegistrarEncuesta")
    void registrarEncuesta(@Body ArrayList<PreguntaEncuestaSolicitudModel> preguntaModelList, Callback<JsonResponse> cb);
}

package app.gamd.contract;

import app.gamd.common.JsonResponse;
import app.gamd.model.SpecialistModel;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Sigcomt on 01/04/2016.
 */
public interface ISpecialistService {
    @POST("/api/Especialista/GetEspecialistas")
    void getEspecialistas(@Body SpecialistModel specialistModel, Callback<JsonResponse> cb);
}

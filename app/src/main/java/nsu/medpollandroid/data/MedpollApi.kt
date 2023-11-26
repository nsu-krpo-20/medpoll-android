package nsu.medpollandroid.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MedpollApi {
    @Headers("accept: application/json")
    @GET("prescriptions")
    suspend fun getPrescriptionsByCard(@Query("cardUUID") cardUuid: String): Response<List<PrescriptionGeneralInfo>>
}
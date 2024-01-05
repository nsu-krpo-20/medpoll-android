package nsu.medpollandroid.data

import nsu.medpollandroid.data.prescriptions.PrescriptionDataFromResponse
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MedpollApi {
    @Headers("accept: application/json")
    @GET("prescriptions")
    suspend fun getPrescriptionsByCard(@Query("cardUUID") cardUuid: String):
            Response<List<PrescriptionEntity>>

    @Headers("accept: application/json")
    @GET("prescriptions/{id}")
    suspend fun getPrescriptionAllInfo(@Path("id") id: Long,
                                       @Query("cardUUID") cardUuid: String):
            Response<PrescriptionDataFromResponse>

    @Headers("accept: application/json")
    @POST("reports")
    suspend fun postReport(@Query("cardUUID") cardUuid: String, @Body body: ReportDataRequest):
            Response<ReportPostResponse>
}
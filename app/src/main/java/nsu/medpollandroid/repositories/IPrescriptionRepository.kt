package nsu.medpollandroid.repositories

import kotlinx.coroutines.flow.Flow
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.ui_data.PrescriptionInfoData

interface IPrescriptionRepository {
    fun getPrescription(id: Int): Flow<PrescriptionInfoData>
    suspend fun getPrescriptions(apiUrl: String, cardUuid: String): List<PrescriptionGeneralInfo>?
}
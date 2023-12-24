package nsu.medpollandroid.repositories

import kotlinx.coroutines.flow.Flow
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity

interface IPrescriptionRepository {
    fun getPrescription(apiUrl: String, id: Long): Flow<PrescriptionInfoData>

    fun getPrescriptionsList(apiUrl: String, cardUuid: String): Flow<List<PrescriptionEntity>>

    fun updateLocalPrescriptions()

    fun updateLocalPrescriptions(apiUrl: String, cardUuid: String)
}
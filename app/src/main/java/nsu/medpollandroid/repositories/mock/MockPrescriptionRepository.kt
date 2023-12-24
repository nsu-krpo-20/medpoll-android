package nsu.medpollandroid.repositories.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import nsu.medpollandroid.repositories.IPrescriptionRepository
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionInfoPreviewProvider
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionsPreviewProvider
import javax.inject.Inject

class MockPrescriptionRepository @Inject constructor(): IPrescriptionRepository {
    override fun getPrescription(apiUrl: String, id: Long): Flow<PrescriptionInfoData> {
        return SamplePrescriptionInfoPreviewProvider()
            .values
            .asFlow()
    }

    override fun updateLocalPrescriptions() {

    }

    override fun updateLocalPrescriptions(apiUrl: String, cardUuid: String) {

    }

    override fun getPrescriptionsList(
        apiUrl: String,
        cardUuid: String
    ): Flow<List<PrescriptionEntity>> {
        return flow {
            emit (SamplePrescriptionsPreviewProvider()
                    .values
                    .map { s -> s.value }
                    .first()
            )
        }
    }
}
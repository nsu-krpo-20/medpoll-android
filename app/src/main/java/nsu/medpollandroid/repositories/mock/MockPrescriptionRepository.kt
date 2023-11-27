package nsu.medpollandroid.repositories.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.repositories.IPrescriptionRepository
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionInfoPreviewProvider
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionsPreviewProvider
import nsu.medpollandroid.ui_data.PrescriptionInfoData
import javax.inject.Inject

class MockPrescriptionRepository @Inject constructor(): IPrescriptionRepository {
    override fun getPrescription(id: Long): Flow<PrescriptionInfoData> {
        return SamplePrescriptionInfoPreviewProvider()
            .values
            .asFlow()
    }

    override suspend fun getPrescriptions(
        apiUrl: String,
        cardUuid: String
    ): List<PrescriptionGeneralInfo>? {
        return SamplePrescriptionsPreviewProvider()
            .values
            .map { s -> s.value }
            .first()
    }
}
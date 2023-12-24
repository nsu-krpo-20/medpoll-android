package nsu.medpollandroid.data.prescriptions

import nsu.medpollandroid.data.prescriptions.db.MedicineEntity
import nsu.medpollandroid.data.prescriptions.db.MetricEntity
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import nsu.medpollandroid.data.prescriptions.db.PrescriptionWithMedsAndMetrics

data class PrescriptionDataFromResponse(
    val id: Long,
    val patientCardId: Long,
    val createdTime: Long,
    val editedTime: Long,
    val meds: List<MedicineFromResponse>,
    val metrics: List<MetricFromResponse>,
    val doctorFullName: String?, //Not implemented by backend yet, have to workaround
    val isActive: Boolean
)

data class MedicineFromResponse(
    val name: String,
    val dose: String,
    val periodType: Long,
    var period: String
)

data class MetricFromResponse(
    val name: String,
    val periodType: Long,
    val period: String
)

fun MetricFromResponse.toDbEntity(prescriptionId: Long): MetricEntity {
    return MetricEntity(
        metricPrescriptionId = prescriptionId,
        metricName = name,
        metricPeriodType = periodType,
        metricPeriod = period
    )
}

fun MedicineFromResponse.toDbEntity(prescriptionId: Long): MedicineEntity {
    return MedicineEntity(
        medPrescriptionId = prescriptionId,
        medName = name,
        dose = dose,
        medPeriodType = periodType,
        medPeriod = period
    )
}

fun PrescriptionDataFromResponse.toDbEntity(apiUrl: String): PrescriptionWithMedsAndMetrics {
    val medsDb = meds.map { it.toDbEntity(id) }
    val metricsDb = metrics.map { it.toDbEntity(id) }
    val actualDoctorNameToPut = doctorFullName ?: "Пирогов Николай Иванович" // See top data class def
    return PrescriptionWithMedsAndMetrics(
        prescription = PrescriptionEntity(id, apiUrl, patientCardId, createdTime,
            editedTime, actualDoctorNameToPut, isActive),
        meds = medsDb,
        metrics = metricsDb
    )
}

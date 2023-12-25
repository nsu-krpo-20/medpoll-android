package nsu.medpollandroid.data.prescriptions.db

import androidx.room.Embedded
import androidx.room.Relation

data class PrescriptionWithMedsAndMetrics(
    @Embedded
    val prescription: PrescriptionEntity,
    @Relation(entity = MedicineEntity::class, parentColumn = "id", entityColumn = "med_prescription_id")
    val meds: List<MedicineEntity>,
    @Relation(entity = MetricEntity::class, parentColumn = "id", entityColumn = "metric_prescription_id")
    val metrics: List<MetricEntity>
)

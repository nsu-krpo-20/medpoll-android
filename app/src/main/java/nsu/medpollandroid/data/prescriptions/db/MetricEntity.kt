package nsu.medpollandroid.data.prescriptions.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metric",
    foreignKeys = [ForeignKey(
        entity = PrescriptionEntity::class,
        parentColumns = ["id"],
        childColumns = ["metric_prescription_id"],
        onDelete = ForeignKey.CASCADE)
    ]
)
data class MetricEntity(
    @PrimaryKey(autoGenerate = true) val metricId: Int = 0,
    @ColumnInfo(name = "metric_prescription_id") val metricPrescriptionId: Long,
    @ColumnInfo(name = "metric_name") val metricName: String,
    @ColumnInfo(name = "metric_period_type") val metricPeriodType: Long,
    @ColumnInfo(name = "metric_period") val metricPeriod: String
)

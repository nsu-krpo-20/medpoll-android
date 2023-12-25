package nsu.medpollandroid.data.prescriptions.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "med",
    foreignKeys = [ForeignKey(
        entity = PrescriptionEntity::class,
        parentColumns = ["id"],
        childColumns = ["med_prescription_id"],
        onDelete = ForeignKey.CASCADE)
    ]
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val medId: Int = 0,
    @ColumnInfo(name = "med_prescription_id") val medPrescriptionId: Long,
    @ColumnInfo(name = "med_name") val medName: String,
    @ColumnInfo(name = "dose") val dose: String,
    @ColumnInfo(name = "med_period_type") val medPeriodType: Long,
    @ColumnInfo(name = "med_period") val medPeriod: String
)

package nsu.medpollandroid.data.prescriptions.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prescription")
data class PrescriptionEntity (
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "card_id") val patientCardId: Long,
    @ColumnInfo(name = "created_time") val createdTime: Long,
    @ColumnInfo(name = "edited_time") val editedTime: Long,
    @ColumnInfo(name = "doctor_full_name") val doctorFullName: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

package nsu.medpollandroid.data.prescriptions.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "prescription", primaryKeys = ["id", "api_url"],
    indices = [Index(value = ["id"], unique = true)])
data class PrescriptionEntity (
    val id: Long,
    @ColumnInfo(name = "api_url") val apiUrl: String,
    @ColumnInfo(name = "card_id") val patientCardId: Long,
    @ColumnInfo(name = "created_time") val createdTime: Long,
    @ColumnInfo(name = "edited_time") val editedTime: Long,
    @ColumnInfo(name = "doctor_full_name") val doctorFullName: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

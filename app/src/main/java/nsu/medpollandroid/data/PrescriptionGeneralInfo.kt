package nsu.medpollandroid.data

data class PrescriptionGeneralInfo (
    val id: Long,
    val patientCardId: Long,
    val createdTime: Long,
    val editedTime: Long,
    val doctorFullName: String,
    val isActive: Boolean
)

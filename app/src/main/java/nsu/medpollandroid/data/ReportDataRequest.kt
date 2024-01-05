package nsu.medpollandroid.data

data class ReportDataRequest(
    val id: Long,
    val prescriptionId: Long,
    val medsTaken: List<Boolean?>,
    val metrics: List<String?>,
    val feedback: String,
    val time: Long
)

data class ReportPostResponse(
    val id: Int
)
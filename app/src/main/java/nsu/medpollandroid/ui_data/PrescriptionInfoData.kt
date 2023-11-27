package nsu.medpollandroid.ui_data

data class TimeOfDay(
    val hours: Int,
    val minutes: Int
) {
    init {
        require(hours in 0..23) {
            "Hours isn't in valid range"
        }
        require(minutes in 0..59) {
            "Minutes isn't in valid range"
        }
    }
}
sealed class PrescriptionPeriod {
    data class NTimesDaily(
        val timestamps: List<TimeOfDay>
    ): PrescriptionPeriod() {
        init {
            require(timestamps.isNotEmpty()) {
                "Timestamps are empty"
            }
        }
    }

    data class EachNDays(
        val period: Int,
        val timestamp: TimeOfDay
    ): PrescriptionPeriod() {
        init {
            require(period > 0) {
                "Period isn't positive"
            }
        }
    }

    data class PerWeekday(
        val weekdays: List<NTimesDaily?> //null value indicates that there are no prescriptions on this weekday
    ): PrescriptionPeriod() {
        init {
            require(weekdays.size == 7) {
                "Weekdays length isn't equal to 7 weekdays"
            }
            require(weekdays.any { weekday -> weekday != null }) {
                "All weekdays are null"
            }
        }
    }

    data class Custom(val description: String): PrescriptionPeriod() {
        init {
            require(description.isNotEmpty()) {
                "Description is empty"
            }
        }
    }
}
data class Medicine(
    val id: Int,
    val name: String,
    val amount: String,
    val period: PrescriptionPeriod
) {
    init {
        require(name.isNotEmpty()) {
            "Name is empty string"
        }
        require(amount.isNotEmpty()) {
            "Amount is empty string"
        }
    }
}

data class Metric(
    val id: Int,
    val name: String,
    val period: PrescriptionPeriod
) {
    init {
        require(name.isNotEmpty()) {
            "Name is empty string"
        }
    }
}
data class PrescriptionInfoData(
    val creationTimestamp: Long,
    val doctorFullName: String,
    val medicines: List<Medicine>,
    val metrics: List<Metric>
) {
    init {
        require(creationTimestamp >= 0) {
            "Timestamp isn't positive"
        }
        require(doctorFullName.isNotEmpty()) {
            "Doctor full name is empty string"
        }
        require(medicines.isNotEmpty() || metrics.isNotEmpty()) {
            "All medicine and metric data is empty"
        }
    }
}
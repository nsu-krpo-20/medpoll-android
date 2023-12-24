package nsu.medpollandroid.data.prescriptions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nsu.medpollandroid.data.prescriptions.db.MedicineEntity
import nsu.medpollandroid.data.prescriptions.db.MetricEntity
import nsu.medpollandroid.data.prescriptions.db.PrescriptionWithMedsAndMetrics

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
    val id: Long,
    val isActive: Boolean,
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

fun periodNTimesDailyFromIntsList(data: List<Int>): PrescriptionPeriod.NTimesDaily {
    val timesOfDay = data.map { TimeOfDay(it / (60*60), it % (60*60)) }
    return PrescriptionPeriod.NTimesDaily(timesOfDay)
}

fun periodNTimesDailyFromRawData(data: String): PrescriptionPeriod.NTimesDaily {
    val gson = Gson();
    val type = object : TypeToken<List<Int>>() {}.type
    val timestamps: List<Int> = gson.fromJson(data, type)
    return periodNTimesDailyFromIntsList(timestamps)
}

fun periodEachNDaysFromRawData(data: String): PrescriptionPeriod.EachNDays {
    val gson = Gson();
    val type = object : TypeToken<List<Int>>() {}.type
    val dataParsed: List<Int> = gson.fromJson(data, type)
    val period = dataParsed[0]
    val timeOfDay = TimeOfDay(dataParsed[1] / (60*60), dataParsed[1] % (60*60))
    return PrescriptionPeriod.EachNDays(period, timeOfDay)
}

fun periodPerWeekdayFromRawData(data: String): PrescriptionPeriod.PerWeekday {
    val gson = Gson();
    data class Schedule(
        val mon: List<Int>?,
        val tue: List<Int>?,
        val wed: List<Int>?,
        val thu: List<Int>?,
        val fri: List<Int>?,
        val sat: List<Int>?,
        val sun: List<Int>?
    )
    val type = object : TypeToken<Schedule>() {}.type
    val dataParsed: Schedule = gson.fromJson(data, type)
    val weekdays: MutableList<PrescriptionPeriod.NTimesDaily?> = mutableListOf()

    /*
    Cursed, I admit, but:
    */
    weekdays.add(if (dataParsed.mon == null) null else periodNTimesDailyFromIntsList(dataParsed.mon))
    weekdays.add(if (dataParsed.tue == null) null else periodNTimesDailyFromIntsList(dataParsed.tue))
    weekdays.add(if (dataParsed.wed == null) null else periodNTimesDailyFromIntsList(dataParsed.wed))
    weekdays.add(if (dataParsed.thu == null) null else periodNTimesDailyFromIntsList(dataParsed.thu))
    weekdays.add(if (dataParsed.fri == null) null else periodNTimesDailyFromIntsList(dataParsed.fri))
    weekdays.add(if (dataParsed.sat == null) null else periodNTimesDailyFromIntsList(dataParsed.sat))
    weekdays.add(if (dataParsed.sun == null) null else periodNTimesDailyFromIntsList(dataParsed.sun))


    return PrescriptionPeriod.PerWeekday(weekdays)
}

fun periodCustomFromRawData(data: String): PrescriptionPeriod.Custom {
    return PrescriptionPeriod.Custom(data)
}

fun periodFromRawData(type: Int, info: String): PrescriptionPeriod {
    return when (type) {
        PeriodTypes.N_TIMES_PER_DAY.ordinal + 1 -> periodNTimesDailyFromRawData(info)
        PeriodTypes.ONCE_PER_N_DAY.ordinal + 1 -> periodEachNDaysFromRawData(info)
        PeriodTypes.WEEK_SCHEDULE.ordinal + 1 -> periodPerWeekdayFromRawData(info)
        PeriodTypes.CUSTOM.ordinal + 1 -> periodCustomFromRawData(info)
        else -> throw IllegalArgumentException()
    }
}

fun MedicineEntity.transformToNormal(): Medicine {
    return Medicine(medId, medName, dose, periodFromRawData(medPeriodType.toInt(), medPeriod))
}

fun MetricEntity.transformToNormal(): Metric {
    return Metric(metricId, metricName, periodFromRawData(metricPeriodType.toInt(), metricPeriod))
}

fun PrescriptionWithMedsAndMetrics.transformToNormal(): PrescriptionInfoData {
    val medsTransformed = meds.map { it.transformToNormal() }
    val metricsTransformed = metrics.map { it.transformToNormal() }
    return PrescriptionInfoData(prescription.id, prescription.isActive,
        prescription.createdTime, prescription.doctorFullName,
        medsTransformed, metricsTransformed)
}

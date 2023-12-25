package nsu.medpollandroid.data.prescriptions

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nsu.medpollandroid.data.prescriptions.db.MedicineEntity
import nsu.medpollandroid.data.prescriptions.db.MetricEntity
import nsu.medpollandroid.data.prescriptions.db.PrescriptionWithMedsAndMetrics
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

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
abstract class PrescriptionPeriod {
    abstract fun isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean
    abstract fun nextFitsAfterMomentDelay(fromMillis: Long, createdTime: Long): Long
}

data class NTimesDaily(
    val timestamps: List<TimeOfDay>
): PrescriptionPeriod() {
    init {
        require(timestamps.isNotEmpty()) {
            "Timestamps are empty"
        }
    }

    override fun isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean {
        val dateFrom = Date(fromMillis)
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        for (timeOfDay in timestamps) {
            val curCalendar = Calendar.getInstance()
            curCalendar.set(Calendar.YEAR, calendarFrom.get(Calendar.YEAR))
            curCalendar.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH))
            curCalendar.set(Calendar.DAY_OF_MONTH, calendarFrom.get(Calendar.DAY_OF_MONTH))
            curCalendar.set(Calendar.HOUR, timeOfDay.hours)
            curCalendar.set(Calendar.MINUTE, timeOfDay.minutes)
            val curMillis = curCalendar.timeInMillis
            if (abs(curMillis - fromMillis) <= distance) {
                return true
            }
        }
        return false
    }

    override fun nextFitsAfterMomentDelay(fromMillis: Long, createdTime: Long): Long {
        val dateFrom = Date(fromMillis)
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        var minDistance = fromMillis
        for (timeOfDay in timestamps) {
            val curCalendar = Calendar.getInstance()
            curCalendar.set(Calendar.YEAR, calendarFrom.get(Calendar.YEAR))
            curCalendar.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH))
            curCalendar.set(Calendar.DAY_OF_MONTH, calendarFrom.get(Calendar.DAY_OF_MONTH))
            curCalendar.set(Calendar.HOUR, timeOfDay.hours)
            curCalendar.set(Calendar.MINUTE, timeOfDay.minutes)
            val curMillis = curCalendar.timeInMillis
            if (curMillis > fromMillis) {
                if (curMillis - fromMillis < minDistance) {
                    minDistance = curMillis - fromMillis
                }
            }
        }
        return minDistance
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

    override fun isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean {
        val intPeriodsBetween = abs(fromMillis - createdTime) / (1000 * 60 * 60 * 24 * period)
        val possiblePointBefore = ((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24) +
                intPeriodsBetween * 1000 * 60 * 60 * 24 * period +
                timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000
        val possiblePointAfter = ((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24) +
                (intPeriodsBetween + 1) * 1000 * 60 * 60 * 24 * period +
                timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000
        if ((abs(fromMillis - possiblePointBefore) <= distance) || (abs(fromMillis - possiblePointAfter) <= distance)) {
            return true
        }
        return false
    }

    override fun nextFitsAfterMomentDelay(fromMillis: Long, createdTime: Long): Long {
        val intPeriodsBetween = abs(fromMillis - createdTime) / (1000 * 60 * 60 * 24 * period)
        val possiblePointAfter = (((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24) +
                (intPeriodsBetween + 1) * 1000 * 60 * 60 * 24 * period +
                timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000)
        var minDistance = fromMillis
        if (possiblePointAfter > fromMillis) {
            if (possiblePointAfter - fromMillis < minDistance) {
                minDistance = possiblePointAfter - fromMillis
            }
        }
        return minDistance
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

    override fun isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean {
        val daysOfWeek = arrayOf(Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
            Calendar.SATURDAY, Calendar.SUNDAY)
        for (day in weekdays) {
            if (day == null) {
                continue
            }
            if (isWithinLengthForDayOfWeekFrom(day.timestamps, fromMillis, distance, daysOfWeek[weekdays.indexOf(day)])) {
                return true
            }
        }
        return false
    }

    override fun nextFitsAfterMomentDelay(fromMillis: Long, createdTime: Long): Long {
        val daysOfWeek = arrayOf(Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
            Calendar.SATURDAY, Calendar.SUNDAY)
        var minDelay = fromMillis
        for (day in weekdays) {
            if (day == null) {
                continue
            }
            if (nextFitsAfterMomentForDayOfWeekFromDelay(day.timestamps, fromMillis,
                    daysOfWeek[weekdays.indexOf(day)]) < minDelay) {
                minDelay = nextFitsAfterMomentForDayOfWeekFromDelay(day.timestamps, fromMillis,
                    daysOfWeek[weekdays.indexOf(day)])
            }
        }
        return minDelay
    }

    private fun isWithinLengthForDayOfWeekFrom(timestamps: List<TimeOfDay>,
                                               fromMillis: Long, distance: Long, dayOfWeek: Int): Boolean {
        val dateFrom = Date(fromMillis)
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        for (timeOfDay in timestamps) {
            val curCalendar = Calendar.getInstance()
            curCalendar.set(Calendar.YEAR, calendarFrom.get(Calendar.YEAR))
            curCalendar.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH))
            curCalendar.set(Calendar.DAY_OF_MONTH, calendarFrom.get(Calendar.DAY_OF_MONTH))
            curCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            curCalendar.set(Calendar.HOUR, timeOfDay.hours)
            curCalendar.set(Calendar.MINUTE, timeOfDay.minutes)
            val curMillis = curCalendar.timeInMillis
            Log.d("Notifs", "abs(curMillis - fromMillis) == " + abs(curMillis - fromMillis))
            Log.d("Notifs", "distance is " + distance)
            if (abs(curMillis - fromMillis) <= distance) {
                return true
            }
        }
        return false
    }

    private fun nextFitsAfterMomentForDayOfWeekFromDelay(timestamps: List<TimeOfDay>,
                                                         fromMillis: Long, dayOfWeek: Int): Long {
        val dateFrom = Date(fromMillis)
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        var minDistance = fromMillis
        for (timeOfDay in timestamps) {
            val curCalendar = Calendar.getInstance()
            curCalendar.set(Calendar.YEAR, calendarFrom.get(Calendar.YEAR))
            curCalendar.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH))
            curCalendar.set(Calendar.DAY_OF_MONTH, calendarFrom.get(Calendar.DAY_OF_MONTH))
            curCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            curCalendar.set(Calendar.HOUR, timeOfDay.hours)
            curCalendar.set(Calendar.MINUTE, timeOfDay.minutes)
            val curMillis = curCalendar.timeInMillis
            if (curMillis > fromMillis) {
                if (curMillis - fromMillis < minDistance) {
                    minDistance = curMillis - fromMillis
                }
            }
        }
        return minDistance
    }
}

data class Custom(val description: String): PrescriptionPeriod() {
    init {
        require(description.isNotEmpty()) {
            "Description is empty"
        }
    }

    override fun isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean {
        return false
    }

    override fun nextFitsAfterMomentDelay(fromMillis: Long, createdTime: Long): Long {
        return Long.MAX_VALUE
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

fun periodNTimesDailyFromIntsList(data: List<Int>): NTimesDaily {
    val timesOfDay = data.map { TimeOfDay(it / (60*60), (it / 60) % 60) }
    return NTimesDaily(timesOfDay)
}

fun periodNTimesDailyFromRawData(data: String): NTimesDaily {
    val gson = Gson();
    val type = object : TypeToken<List<Int>>() {}.type
    val timestamps: List<Int> = gson.fromJson(data, type)
    return periodNTimesDailyFromIntsList(timestamps)
}

fun periodEachNDaysFromRawData(data: String): EachNDays {
    val gson = Gson();
    val type = object : TypeToken<List<Int>>() {}.type
    val dataParsed: List<Int> = gson.fromJson(data, type)
    val period = dataParsed[0]
    val timeOfDay = TimeOfDay(dataParsed[1] / (60*60), (dataParsed[1] / 60) % 60)
    return EachNDays(period, timeOfDay)
}

fun periodPerWeekdayFromRawData(data: String): PerWeekday {
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
    val weekdays: MutableList<NTimesDaily?> = mutableListOf()

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


    return PerWeekday(weekdays)
}

fun periodCustomFromRawData(data: String): Custom {
    return Custom(data)
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

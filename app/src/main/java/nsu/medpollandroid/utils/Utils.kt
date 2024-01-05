package nsu.medpollandroid.utils

import androidx.compose.ui.res.stringResource
import nsu.medpollandroid.R
import nsu.medpollandroid.data.prescriptions.Custom
import nsu.medpollandroid.data.prescriptions.EachNDays
import nsu.medpollandroid.data.prescriptions.NTimesDaily
import nsu.medpollandroid.data.prescriptions.PerWeekday
import nsu.medpollandroid.data.prescriptions.PrescriptionPeriod
import nsu.medpollandroid.data.prescriptions.TimeOfDay
import nsu.medpollandroid.ui.PeriodInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun <T> listOfWeekdays(
    monday: T? = null,
    tuesday: T? = null,
    wednesday: T? = null,
    thursday: T? = null,
    friday: T? = null,
    saturday: T? = null,
    sunday: T? = null,
): List<T?> {
    return listOf(
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        saturday,
        sunday
    )
}

fun getDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy hh : mm")
    val netDate = Date(timestamp)
    return sdf.format(netDate)
}

fun String.mul(value: Int): String {
    if (value <= 0) {
        throw IllegalArgumentException("Until must be positive")
    }

    val builder = StringBuilder()
    for (i in 0..< value) {
        builder.append(this)
    }
    return builder.toString()
}

fun String.leftZeroPad(until: Int): String {
    if (until < 0) {
        throw IllegalArgumentException("Until must be positive")
    }

    if (until <= length) {
        return this
    }

    return "0".mul(until - length) + this
}
fun TimeOfDay.format(): String {
    return hours.toString().leftZeroPad(2) +
            " : " +
            minutes.toString().leftZeroPad(2)
}

fun Int.leftZeroPad(until: Int): String {
    return toString().leftZeroPad(until)
}

fun Int.daysString(): String {
    if (this < 0) {
        throw IllegalArgumentException("Times integer must be positive")
    }

    if (this == 1) {
        return "день"
    }

    if ((this / 10) % 10 == 1) {
        return "дней"
    }

    if (this % 10 in 2..4) {
        return "дня"
    }

    return "дней"
}

fun periodValidOn(period: PrescriptionPeriod, timestamp: Long, date: Date): Boolean {
    val millisecondsInDay = 24 * 60 * 60 * 1000
    val startDate = Date((timestamp / millisecondsInDay) * millisecondsInDay)
    val daysFromStart = (date.time - startDate.time) / millisecondsInDay
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    val weekday = calendar.get(Calendar.DAY_OF_WEEK)
    return when (period) {
        is Custom -> daysFromStart >= 0
        is EachNDays -> daysFromStart >= 0 && daysFromStart % period.period == 0L
        is NTimesDaily -> daysFromStart >= 0
        is PerWeekday -> period.weekdays[weekday - 1] != null && daysFromStart >= 0
        else -> false
    }
}

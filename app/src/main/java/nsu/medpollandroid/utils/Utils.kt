package nsu.medpollandroid.utils

import nsu.medpollandroid.data.prescriptions.TimeOfDay
import java.text.SimpleDateFormat
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

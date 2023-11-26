package nsu.medpollandroid.utils

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
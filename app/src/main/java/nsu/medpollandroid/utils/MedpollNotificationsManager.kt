package nsu.medpollandroid.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nsu.medpollandroid.MainActivity
import nsu.medpollandroid.MedpollApplication
import nsu.medpollandroid.R
import nsu.medpollandroid.data.prescriptions.Medicine
import nsu.medpollandroid.data.prescriptions.Metric
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.PrescriptionPeriod
import nsu.medpollandroid.data.prescriptions.TimeOfDay
import nsu.medpollandroid.repositories.IRepositories
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MedpollNotificationsManager (
    val context: Context,
    val repositories: IRepositories,
    private val workManager: WorkManager
) {
    private fun getPrescriptionNotifyingJobName(prescriptionInfoData: PrescriptionInfoData): String {
        return (prescriptionInfoData.creationTimestamp.toString() + ".presc:"
                + prescriptionInfoData.doctorFullName)
    }

    private fun PrescriptionPeriod.NTimesDaily.nextFitsAfterMomentDelay(fromMillis: Long): Long {
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

    private fun PrescriptionPeriod.EachNDays.nextFitsAfterMomentDelay(fromMillis: Long,
                                                                      createdTime: Long): Long {
        val intPeriodsBetween = abs(fromMillis - createdTime) / (1000 * 60 * 60 * 24 * period)
        val possiblePointAfter = (((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24)
                                    + (intPeriodsBetween + 1) * 1000 * 60 * 60 * 24 * period
                                        + timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000)
        var minDistance = fromMillis
        if (possiblePointAfter > fromMillis) {
            if (possiblePointAfter - fromMillis < minDistance) {
                minDistance = possiblePointAfter - fromMillis
            }
        }
        return minDistance
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

    private fun PrescriptionPeriod.PerWeekday.nextFitsAfterMomentDelay(fromMillis: Long): Long {
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

    private fun PrescriptionPeriod.nextFitsAfterMomentDelay(fromMillis: Long,
                                                            createdTime: Long): Long {
        when(this) {
            is PrescriptionPeriod.NTimesDaily -> {
                return (this as PrescriptionPeriod.NTimesDaily).nextFitsAfterMomentDelay(
                    fromMillis
                )
            }
            is PrescriptionPeriod.EachNDays -> {
                return (this as PrescriptionPeriod.EachNDays).nextFitsAfterMomentDelay(
                    fromMillis,
                    createdTime
                )
            }
            is PrescriptionPeriod.PerWeekday -> {
                return (this as PrescriptionPeriod.PerWeekday).nextFitsAfterMomentDelay(
                    fromMillis
                )
            }
            else -> {
                return fromMillis
            }
        }
    }

    private fun calculateMinimumDelayToNextNotification(prescriptionInfoData: PrescriptionInfoData,
                                                        curTime: Long): Long {
        var minDelay = curTime
        for (med in prescriptionInfoData.medicines) {
            val value =
                med.period.nextFitsAfterMomentDelay(curTime, prescriptionInfoData.creationTimestamp)
            if (value < minDelay) {
                minDelay = value
            }
        }
        for (metric in prescriptionInfoData.metrics) {
            val value =
                metric.period.nextFitsAfterMomentDelay(curTime, prescriptionInfoData.creationTimestamp)
            if (value < minDelay) {
                minDelay = value
            }
        }
        return minDelay
    }

    private fun PrescriptionPeriod.NTimesDaily.isWithinLengthFrom(fromMillis: Long, distance: Long): Boolean {
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

    private fun PrescriptionPeriod.EachNDays.isWithinLengthFrom(fromMillis: Long,
                                                                distance: Long,
                                                                createdTime: Long): Boolean {
        val intPeriodsBetween = abs(fromMillis - createdTime) / (1000 * 60 * 60 * 24 * period)
        val possiblePointBefore = ((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24)
                                    + intPeriodsBetween * 1000 * 60 * 60 * 24 * period
                                        + timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000
        val possiblePointAfter = ((createdTime / 1000 * 60 * 60 * 24) * 1000 * 60 * 60 * 24)
                                    + (intPeriodsBetween + 1) * 1000 * 60 * 60 * 24 * period
                                        + timestamp.hours * 60 * 60 * 1000 + timestamp.minutes * 60 * 1000
        if ((abs(fromMillis - possiblePointBefore) <= distance) || (abs(fromMillis - possiblePointAfter) <= distance)) {
            return true
        }
        return false
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

    private fun PrescriptionPeriod.PerWeekday.isWithinLengthFrom(fromMillis: Long, distance: Long): Boolean {
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

    private fun PrescriptionPeriod.isWithinLengthFrom(fromMillis: Long, distance: Long, createdTime: Long): Boolean {
        when(this) {
            is PrescriptionPeriod.NTimesDaily -> {
                return (this as PrescriptionPeriod.NTimesDaily).isWithinLengthFrom(
                    fromMillis,
                    distance
                )
            }
            is PrescriptionPeriod.EachNDays -> {
                return (this as PrescriptionPeriod.EachNDays).isWithinLengthFrom(
                    fromMillis,
                    distance,
                    createdTime
                )
            }
            is PrescriptionPeriod.PerWeekday -> {
                return (this as PrescriptionPeriod.PerWeekday).isWithinLengthFrom(
                    fromMillis,
                    distance
                )
            }
            else -> {
                return false // We have nothing to do with 'CUSTOM'
            }
        }
    }
    private fun chooseMedsToNotify(prescriptionInfoData: PrescriptionInfoData, from: Long): List<Medicine> {
        val result = mutableListOf<Medicine>()
        for (med in prescriptionInfoData.medicines) {
            if (med.period.isWithinLengthFrom(from, UNIFICATION_OF_NOTIFICATIONS_DELAY_MS,
                    prescriptionInfoData.creationTimestamp)) {
                result.add(med)
            }
        }
        return result
    }

    private fun chooseMetricsToNotify(prescriptionInfoData: PrescriptionInfoData, from: Long): List<Metric> {
        val result = mutableListOf<Metric>()
        for (metric in prescriptionInfoData.metrics) {
            if (metric.period.isWithinLengthFrom(from, UNIFICATION_OF_NOTIFICATIONS_DELAY_MS,
                    prescriptionInfoData.creationTimestamp)) {
                result.add(metric)
            }
        }
        return result
    }

    private fun getTextFor(meds: List<Medicine>, metrics: List<Metric>): String {
        val result: StringBuilder = StringBuilder("Принять медикаменты:\n")
        for (med in meds) {
            result.append(med.name + ", " + med.amount + "\n")
        }
        result.append("Провести замеры:\n")
        for (metric in metrics) {
            result.append(metric.name + "\n")
        }
        return result.toString()
    }

    /**
     * This method is adding next job in chain and breaking it if prescription becomes !isActive
     */
    suspend fun notifyAndScheduleNext(apiUrl: String, id: Long) {
        Log.d("Notifs", "Scheduling notifications")
        val prescriptionInfoData = repositories.prescriptionRepository.getPrescription(apiUrl, id).first()
        val jobName = getPrescriptionNotifyingJobName(prescriptionInfoData)
        if (!prescriptionInfoData.isActive) {
            notifyingJobsChainExists.remove(jobName)
            Log.d("Notifs", "Ret")
            return
        }
        notifyingJobsChainExists[jobName] = true
        val curTime = System.currentTimeMillis()
        val medsToNotify = chooseMedsToNotify(prescriptionInfoData, curTime)
        val metricsToNotify = chooseMetricsToNotify(prescriptionInfoData, curTime)
        if (medsToNotify.isNotEmpty() || metricsToNotify.isNotEmpty()) {
            Log.d("Notifs", "Have smth")
            val text = getTextFor(medsToNotify, metricsToNotify)
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(context, MedpollApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Medpoll: напоминание")
                .setContentText("Есть напоминания о назначениях")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("Permissions", "No permission")
                    return
                }
                notify(1, builder.build())
            }
        }
        else {
            Log.d("Notifs", "Nothing to schedule")
        }

        val delay = calculateMinimumDelayToNextNotification(prescriptionInfoData,
                curTime + UNIFICATION_OF_NOTIFICATIONS_DELAY_MS)
        val inputData = Data.Builder()
            .putString("apiUrl", apiUrl)
            .putLong("id", id)
            .build()
        val workRequestBuilder = OneTimeWorkRequestBuilder<NotifyContinuationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        workManager.enqueue(workRequestBuilder.build())
    }

    /**
     * This method can be called from outside and begins scheduling first job in chain
     */
    fun scheduleNotificationIfNecessary(apiUrl:String, id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Notifs", "Enter scheduling coroutine")
            val prescriptionInfoData = repositories.prescriptionRepository.getPrescription(apiUrl, id).first()
            if (!prescriptionInfoData.isActive) {
                return@launch
            }
            val jobName = getPrescriptionNotifyingJobName(prescriptionInfoData)
            if (notifyingJobsChainExists[jobName] == true) {
                return@launch // Job chain will continue itself if it is still needed
            }
            notifyAndScheduleNext(apiUrl, id)
        }
    }

    inner class NotifyContinuationWorker(
        context: Context,
        workerParameters: WorkerParameters
    ) : Worker(context, workerParameters) {

        override fun doWork(): Result {
            val apiUrl = inputData.getString("apiUrl")
            val id = inputData.getLong("id", 0)
            if (apiUrl == null) {
                return Result.failure()
            }
            sched(apiUrl, id)
            return Result.success()
        }

        private fun sched(apiUrl: String, id: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                notifyAndScheduleNext(apiUrl, id)
            }
        }
    }

    private val notifyingJobsChainExists: MutableMap<String, Boolean> = mutableMapOf()

    companion object {
        private const val UNIFICATION_OF_NOTIFICATIONS_DELAY_MS: Long = 5 * 60000L
    }
}

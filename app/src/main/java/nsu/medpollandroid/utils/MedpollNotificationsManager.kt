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
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject
import kotlin.math.abs

class MedpollNotificationsManager @Inject constructor(
    @ApplicationContext val context: Context,
    val repositories: IRepositories,
    val workManager: WorkManager
) {
    private fun getPrescriptionNotifyingJobName(prescriptionInfoData: PrescriptionInfoData): String {
        return (prescriptionInfoData.creationTimestamp.toString() + ".presc:"
                + prescriptionInfoData.doctorFullName)
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
        val result: StringBuilder = StringBuilder()
        if (meds.isNotEmpty()) {
            result.append("Принять медикаменты:\n")
            for (med in meds) {
                result.append(med.name + ", " + med.amount + "\n")
            }
        }
        if (metrics.isNotEmpty()) {
            result.append("Провести замеры:\n")
            for (metric in metrics) {
                result.append(metric.name + "\n")
            }
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
                .setSmallIcon(R.drawable.local_hospital_fill0_wght400_grad0_opsz24)
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

package nsu.medpollandroid.utils

import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.repositories.IRepositories
import javax.inject.Inject
/*
class MedpollNotificationsManager @Inject constructor(
    val repositories: IRepositories,
    val workManager: WorkManager
) {
    private fun getPrescriptionNotifyingJobName(prescriptionInfoData: PrescriptionInfoData): String {
        return (prescriptionInfoData.creationTimestamp.toString() + ".presc:"
                + prescriptionInfoData.doctorFullName)
    }



    private suspend fun scheduleNextNotification(id: Long) {
        // This method is adding next job in chain and breaking it if prescription becomes !isActive
        val prescriptionInfoData = repositories.prescriptionRepository.getPrescription(id).first()
        if (!prescriptionInfoData.isActive) {
            return
        }

    }

    fun scheduleNotificationIfNecessary(id: Long) {
        // This method can be called from outside and begins scheduling first job in chain
        CoroutineScope(Dispatchers.IO).launch {
            val prescriptionInfoData = repositories.prescriptionRepository.getPrescription(id).first()
            if (!prescriptionInfoData.isActive) {
                return@launch
            }
            val jobName = getPrescriptionNotifyingJobName(prescriptionInfoData)
            if (notifyingJobsChainExists[jobName] == true) {
                return@launch // Job chain will continue itself if it is still needed
            }
            scheduleNextNotification(id)
        }
    }

    private val notifyingJobsChainExists: Map<String, Boolean> = emptyMap()


}

 */

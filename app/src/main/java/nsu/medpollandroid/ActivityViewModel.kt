package nsu.medpollandroid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import nsu.medpollandroid.repositories.IRepositories
import nsu.medpollandroid.utils.MedpollNotificationsManager

class ActivityViewModel(
    private val repositories: IRepositories,
    private val medpollNotificationsManager: MedpollNotificationsManager
) : ViewModel() {
    private val cardsListMutableStateFlow = MutableStateFlow(emptyList<Card>())

    val cardsListStateFlow: StateFlow<List<Card>> = cardsListMutableStateFlow

    init {
        viewModelScope.launch {
            repositories.prescriptionRepository.updateLocalPrescriptions()
            repositories.cardRepository.getAllCardsFlow()
                .collect { cardsList ->
                    cardsListMutableStateFlow.value = cardsList
                }
        }
    }

    private val prescriptionsInfoListMutableStateFlow = MutableStateFlow(emptyList<PrescriptionEntity>())

    val prescriptionsInfoListStateFlow: StateFlow<List<PrescriptionEntity>> =
        prescriptionsInfoListMutableStateFlow

    private var apiUrl: String? = null
    private var cardUuid: String? = null
    private var curPrescriptionsWatchJob: Job? = null

    fun readLocalPrescriptionsListFor(apiUrl: String, cardUuid: String) {
        if ((this.apiUrl != apiUrl) || (this.cardUuid != cardUuid)) {
            prescriptionsInfoListMutableStateFlow.value = emptyList()
            this.apiUrl = apiUrl
            this.cardUuid = cardUuid
            curPrescriptionsWatchJob?.cancel()
            curPrescriptionsWatchJob = viewModelScope.launch {
                repositories.prescriptionRepository.getPrescriptionsList(apiUrl, cardUuid)
                    .collect { prescriptionsList ->
                        prescriptionsInfoListMutableStateFlow.value = prescriptionsList
                        for (prescription in prescriptionsList) {
                            medpollNotificationsManager.scheduleNotificationIfNecessary(apiUrl, prescription.id)
                        }
                    }
            }
        }
    }

    fun updateLocalPrescriptionsListFor(apiUrl: String, cardUuid: String) {
        repositories.prescriptionRepository.updateLocalPrescriptions(apiUrl, cardUuid)
    }

    private val prescriptionMutableStateFlow = MutableStateFlow<PrescriptionInfoData?>(null)

    val prescriptionStateFlow: StateFlow<PrescriptionInfoData?> = prescriptionMutableStateFlow

    private var prescriptionId: Long? = null
    private var curUrl: String? = null
    private var curPrescriptionWatchJob: Job? = null

    fun readPrescriptionFor(apiUrl: String, id: Long) {
        if ((prescriptionId != id) || (curUrl != apiUrl)) {
            prescriptionMutableStateFlow.value = null
            prescriptionId = id
            curUrl = apiUrl
            curPrescriptionWatchJob?.cancel()
            curPrescriptionWatchJob = viewModelScope.launch {
                repositories.prescriptionRepository.getPrescription(apiUrl, id)
                    .collect { prescriptionInfo ->
                        prescriptionMutableStateFlow.value = prescriptionInfo
                    }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return ActivityViewModel (
                    (application as MedpollApplication).repositories,
                    (application as MedpollApplication).medpollNotificationsManager
                ) as T
            }
        }
    }

}

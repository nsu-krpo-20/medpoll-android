package nsu.medpollandroid

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.repositories.DataRepository
import nsu.medpollandroid.repositories.IRepositories
import nsu.medpollandroid.repositories.Repositories

class ActivityViewModel(
    private val repositories: IRepositories
) : ViewModel() {
    private val listMutableStateFlow = MutableStateFlow(emptyList<Card>())

    val listStateFlow: StateFlow<List<Card>> = listMutableStateFlow

    init {
        viewModelScope.launch {
            repositories.cardRepository.getAll()
                .collect { cardsList ->
                    listMutableStateFlow.value = cardsList
                }
        }
    }

    private val prescriptionsInfoListMutableState: MutableState<List<PrescriptionGeneralInfo>> =
        mutableStateOf(emptyList())

    val prescriptionsInfoListState: State<List<PrescriptionGeneralInfo>> =
        prescriptionsInfoListMutableState

    private var apiUrl: String? = null
    private var cardUuid: String? = null

    fun updatePrescriptionsListFor(apiUrl: String, cardUuid: String, failure: MutableState<Boolean>) {
        if ((this.apiUrl != apiUrl) || (this.cardUuid != cardUuid)) {
            prescriptionsInfoListMutableState.value = emptyList()
            this.apiUrl = apiUrl
            this.cardUuid = cardUuid
        }
        viewModelScope.launch {
            val requestResult =
                repositories.prescriptionRepository.getPrescriptions(apiUrl, cardUuid)
            if (requestResult != null) {
                prescriptionsInfoListMutableState.value = requestResult
            }
            else {
                Log.e("NET", "Updating prescriptions list failed")
                failure.value = true
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
                    (application as MedpollApplication).repositories
                ) as T
            }
        }
    }

}

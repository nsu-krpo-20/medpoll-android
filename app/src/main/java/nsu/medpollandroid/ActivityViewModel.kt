package nsu.medpollandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.repositories.DataRepository

class ActivityViewModel(
    private val cardsDataRepository: DataRepository
) : ViewModel() {
    private val listMutableStateFlow = MutableStateFlow(emptyList<Card>())

    val listStateFlow: StateFlow<List<Card>> = listMutableStateFlow

    init {
        viewModelScope.launch {
            cardsDataRepository.getAll()
                .collect { cardsList ->
                    listMutableStateFlow.value = cardsList
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
                    (application as MedpollApplication).dataRepository
                ) as T
            }
        }
    }

}

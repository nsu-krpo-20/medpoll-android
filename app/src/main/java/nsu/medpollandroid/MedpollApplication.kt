package nsu.medpollandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import nsu.medpollandroid.repositories.IRepositories
import nsu.medpollandroid.utils.Production
import javax.inject.Inject


@HiltAndroidApp
class MedpollApplication : Application() {
    //@Inject lateinit var cardsDatabase: CardsDatabase

    //@Inject lateinit var dataRepository: DataRepository
    //@Inject lateinit var mockCardRepository: MockCardRepository
    //@Inject lateinit var mockPrescriptionRepository: MockPrescriptionRepository

    @Production
    @Inject lateinit var repositories: IRepositories
}
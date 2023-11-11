package nsu.medpollandroid

import android.app.Application
import nsu.medpollandroid.data.CardsDatabase
import nsu.medpollandroid.repositories.CardsDataRepository

class MedpollApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CardsDatabase.initInstance(this)
        cardsDataRepository = CardsDataRepository.getInstance(this)
    }

    lateinit var cardsDataRepository: CardsDataRepository
        private set
}
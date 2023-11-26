package nsu.medpollandroid

import android.app.Application
import nsu.medpollandroid.data.cards.CardsDatabase
import nsu.medpollandroid.repositories.DataRepository

class MedpollApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CardsDatabase.initInstance(this)
        dataRepository = DataRepository.getInstance(this)
    }

    lateinit var dataRepository: DataRepository
        private set
}
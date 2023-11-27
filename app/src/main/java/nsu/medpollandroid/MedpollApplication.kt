package nsu.medpollandroid

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.data.cards.CardsDatabase
import nsu.medpollandroid.repositories.DataRepository
import nsu.medpollandroid.repositories.IRepositories
import nsu.medpollandroid.repositories.mock.MockCardRepository
import nsu.medpollandroid.repositories.mock.MockPrescriptionRepository
import nsu.medpollandroid.utils.Production
import javax.inject.Inject
import javax.inject.Singleton


@HiltAndroidApp
class MedpollApplication : Application() {
    @Inject lateinit var cardsDatabase: CardsDatabase

    @Inject lateinit var dataRepository: DataRepository
    @Inject lateinit var mockCardRepository: MockCardRepository
    @Inject lateinit var mockPrescriptionRepository: MockPrescriptionRepository

    @Production
    @Inject lateinit var repositories: IRepositories
}
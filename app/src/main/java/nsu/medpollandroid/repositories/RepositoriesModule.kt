package nsu.medpollandroid.repositories

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.repositories.mock.MockCardRepository
import nsu.medpollandroid.repositories.mock.MockPrescriptionRepository
import nsu.medpollandroid.repositories.mock.MockRepositories
import nsu.medpollandroid.utils.MedpollNotificationsManager
import nsu.medpollandroid.utils.Mock
import nsu.medpollandroid.utils.Production
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {
    /*
    @Singleton
    @Mock
    @Provides
    fun provideMockRepositories(
        prescriptionRepository: MockPrescriptionRepository,
        cardRepository: MockCardRepository
    ): IRepositories {
        return MockRepositories(
            prescriptionRepository,
            cardRepository
        )
    }
    */

    @Singleton
    @Provides
    fun provideRepositories(
        dataRepository: DataRepository
    ): IRepositories {
        return Repositories(
            dataRepository
        )
    }
}
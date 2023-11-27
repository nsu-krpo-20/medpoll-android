package nsu.medpollandroid.repositories

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.MedpollApplication
import nsu.medpollandroid.repositories.mock.MockCardRepository
import nsu.medpollandroid.repositories.mock.MockPrescriptionRepository
import nsu.medpollandroid.repositories.mock.MockRepositories
import nsu.medpollandroid.utils.Production
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {
    @Singleton
    @Provides
    @Production
    fun provideMockRepositories(
        prescriptionRepository: MockPrescriptionRepository,
        cardRepository: MockCardRepository
    ): IRepositories {
        return MockRepositories(
            prescriptionRepository,
            cardRepository
        )
    }
}
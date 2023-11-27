package nsu.medpollandroid.repositories.mock

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import nsu.medpollandroid.MedpollApplication
import nsu.medpollandroid.repositories.ICardRepository
import nsu.medpollandroid.repositories.IPrescriptionRepository
import nsu.medpollandroid.repositories.IRepositories
import nsu.medpollandroid.utils.Mock
import nsu.medpollandroid.utils.Production
import javax.inject.Inject
import javax.inject.Singleton

class MockRepositories constructor(
    override val prescriptionRepository: MockPrescriptionRepository,
    override val cardRepository: MockCardRepository
): IRepositories {

}
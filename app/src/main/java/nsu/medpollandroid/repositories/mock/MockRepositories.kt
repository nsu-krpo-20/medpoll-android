package nsu.medpollandroid.repositories.mock

import nsu.medpollandroid.repositories.IRepositories

class MockRepositories constructor(
    override val prescriptionRepository: MockPrescriptionRepository,
    override val cardRepository: MockCardRepository
): IRepositories {

}
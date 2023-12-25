package nsu.medpollandroid.repositories

class Repositories constructor(val dataRepository: DataRepository): IRepositories {
    override val prescriptionRepository: IPrescriptionRepository
        get() = dataRepository

    override val cardRepository: ICardRepository
        get() = dataRepository
}
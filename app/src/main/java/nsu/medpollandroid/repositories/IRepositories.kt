package nsu.medpollandroid.repositories

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.MedpollApplication

interface IRepositories {
    val prescriptionRepository: IPrescriptionRepository
    val cardRepository: ICardRepository
}
package nsu.medpollandroid.repositories

import kotlinx.coroutines.flow.Flow
import nsu.medpollandroid.data.cards.Card

interface ICardRepository {
    fun getAll(): Flow<List<Card>>
    fun delete(card: Card)
    fun insert(card: Card)
}
package nsu.medpollandroid.repositories

import kotlinx.coroutines.flow.Flow
import nsu.medpollandroid.data.cards.Card

interface ICardRepository {
    fun getAllCardsFlow(): Flow<List<Card>>
    fun deleteCard(card: Card)
    fun insertCard(card: Card)
}
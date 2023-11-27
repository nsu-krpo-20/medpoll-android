package nsu.medpollandroid.repositories.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.repositories.ICardRepository
import nsu.medpollandroid.ui.previewproviders.SampleCardsPreviewProvider
import javax.inject.Inject

class MockCardRepository @Inject constructor(): ICardRepository  {
    override fun getAll(): Flow<List<Card>> {
        return SampleCardsPreviewProvider()
            .values
            .map { s -> s.value }
            .asFlow()
    }

    override fun delete(card: Card) {
    }

    override fun insert(card: Card) {
    }
}
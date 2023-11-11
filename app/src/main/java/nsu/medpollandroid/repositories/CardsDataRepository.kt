package nsu.medpollandroid.repositories

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.Card
import nsu.medpollandroid.data.CardsDatabase

class CardsDataRepository private constructor(
    database: CardsDatabase
) {
    private val cardsDatabase = database

    fun getAll(): Flow<List<Card>> {
        return cardsDatabase.cardDao().getAll()
    }

    fun insert(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().insert(card)
        }
    }

    fun delete(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().delete(card)
        }
    }

    companion object {
        private var instance: CardsDataRepository? = null;

        @Synchronized fun initInstance(context: Context) {
            if (instance == null) {
                instance = CardsDataRepository(CardsDatabase.getInstance(context))
            }
        }

        @Synchronized fun getInstance(context: Context): CardsDataRepository {
            if (instance == null) {
                initInstance(context)
            }
            return instance!!
        }
    }
}

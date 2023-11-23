package nsu.medpollandroid.repositories

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.data.cards.CardsDatabase

class DataRepository private constructor(
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
        private var instance: DataRepository? = null;

        @Synchronized fun initInstance(context: Context) {
            if (instance == null) {
                instance = DataRepository(CardsDatabase.getInstance(context))
            }
        }

        @Synchronized fun getInstance(context: Context): DataRepository {
            if (instance == null) {
                initInstance(context)
            }
            return instance!!
        }
    }
}

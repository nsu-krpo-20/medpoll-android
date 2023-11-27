package nsu.medpollandroid.repositories

import android.content.Context
import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.data.cards.CardsDatabase
import nsu.medpollandroid.data.MedpollApi
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.ui_data.PrescriptionInfoData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

class DataRepository @Inject constructor(
    database: CardsDatabase
): IPrescriptionRepository, ICardRepository {
    private val cardsDatabase = database

    override fun getAll(): Flow<List<Card>> {
        return cardsDatabase.cardDao().getAll()
    }

    override fun insert(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().insert(card)
        }
    }

    override fun delete(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().delete(card)
        }
    }

    private var apiUrl: String? = null;
    private var cardUuid: String? = null;
    private var medpollApi: MedpollApi? = null

    private fun setCardData(apiUrl: String, cardUuid: String) {
        this.cardUuid = cardUuid
        if (this.apiUrl != apiUrl) {
            this.apiUrl = apiUrl
            val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            medpollApi = retrofit.create(MedpollApi::class.java)
        }
    }

    override suspend fun getPrescriptions(apiUrl: String, cardUuid: String): List<PrescriptionGeneralInfo>? {
        if ((this.apiUrl != apiUrl) || (this.cardUuid != cardUuid)) {
            setCardData(apiUrl, cardUuid)
        }
        return try {
            val getPrescriptionsCall = medpollApi?.getPrescriptionsByCard(cardUuid)
            getPrescriptionsCall?.body()
        } catch (e: IOException) {
            Log.e("NET", "Fatal exception on request")
            null
        }
    }

    /*companion object {
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
    }*/

    override fun getPrescription(id: Int): Flow<PrescriptionInfoData> {
        TODO("Not yet implemented")
    }
}

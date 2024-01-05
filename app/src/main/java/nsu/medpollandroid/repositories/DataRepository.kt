package nsu.medpollandroid.repositories

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.MedpollApi
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.data.cards.CardsDatabase
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import nsu.medpollandroid.data.prescriptions.db.PrescriptionsDatabase
import nsu.medpollandroid.data.prescriptions.toDbEntity
import nsu.medpollandroid.data.prescriptions.transformToNormal
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val cardsDatabase: CardsDatabase,
    private val prescriptionsDatabase: PrescriptionsDatabase
): IPrescriptionRepository, ICardRepository {
    override fun getAllCardsFlow(): Flow<List<Card>> {
        return cardsDatabase.cardDao().getAll()
    }

    override fun insertCard(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().insert(card)
        }
    }

    override fun deleteCard(card: Card) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDatabase.cardDao().delete(card)
        }
    }

    private suspend fun updateCardPrescriptions(apiUrl: String, cardUuid: String) {
        val api = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedpollApi::class.java)
        val listResponse = api.getPrescriptionsByCard(cardUuid)
        Log.d("Request", "GOT: " + listResponse + ", body: " + listResponse.body())
        if (!listResponse.isSuccessful) {
            Log.w("NET",
                "Request to " + apiUrl + " for card " + cardUuid + " returned "
                        + listResponse.code() + ", body: " + listResponse.body())
            return
        }
        val prescriptionsGeneralInfoList = listResponse.body()
        if (prescriptionsGeneralInfoList == null) {
            Log.e("Something impossible",
                "Response for list is successful, but body is empty")
            return
        }
        for (prescriptionGeneralInfo in prescriptionsGeneralInfoList) {
            val allInfoResponse = api.getPrescriptionAllInfo(prescriptionGeneralInfo.id, cardUuid)
            Log.d("Request", "GOT: " + allInfoResponse + ", body: " + allInfoResponse.body())
            if (!allInfoResponse.isSuccessful) {
                Log.w("NET",
                    "Request to " + apiUrl + ", card " + cardUuid
                            + " for prescription " + prescriptionGeneralInfo
                            + " returned " + allInfoResponse.code()
                            + ", body: " + allInfoResponse.body())
                continue
            }
            val prescriptionsAllInfo = allInfoResponse.body()
            if (prescriptionsAllInfo == null) {
                Log.e("Something impossible",
                    "Response for prescription is successful, but body is empty")
                continue
            }
            prescriptionsDatabase.prescriptionsDao().insertAllPrescriptionData(
                prescriptionsAllInfo.toDbEntity(apiUrl)
            )
        }
    }

    override fun updateLocalPrescriptions() {
        val coroutineExceptionHandler = CoroutineExceptionHandler {_, throwable ->
            throwable.printStackTrace()
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val cards = getAllCardsFlow().first()
            var curUrl: String? = null
            for (card in cards) {
                val cardUuid = card.cardUuid
                if (curUrl != card.apiUrl) {
                    curUrl = card.apiUrl
                }
                updateCardPrescriptions(curUrl, cardUuid)
            }
        }
    }

    override fun updateLocalPrescriptions(apiUrl: String, cardUuid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            updateCardPrescriptions(apiUrl, cardUuid)
        }
    }

    override fun getPrescriptionsList(apiUrl: String, cardUuid: String): Flow<List<PrescriptionEntity>> {
        return prescriptionsDatabase.prescriptionsDao().getPrescriptionsGeneralInfo(apiUrl)
    }

    override fun getPrescription(apiUrl: String, id: Long): Flow<PrescriptionInfoData> {
        return prescriptionsDatabase.prescriptionsDao().getPrescriptionAllInfo(apiUrl, id)
            .map { it.transformToNormal() }
    }
}

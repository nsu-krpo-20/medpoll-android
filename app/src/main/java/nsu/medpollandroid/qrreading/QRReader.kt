package nsu.medpollandroid.qrreading

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.integerResource
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nsu.medpollandroid.db.Card
import nsu.medpollandroid.db.CardsDatabase

class QRReader {
    companion object {
        private const val uuidLength = 36 // 32 + 4 * '-'
        fun read(context: Context, nameForNew: String, readingResult: ReadingResult) {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = GmsBarcodeScanning.getClient(context, options)
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue: String? = barcode.rawValue
                    Log.d("QR", "Got: $rawValue")
                    if (rawValue == null) {
                        Log.e("QR", "Empty msg")
                        readingResult.result = ReadingResult.PossibleResults.FAILURE
                    }
                    else {
                        val dataLength: Int = rawValue.length;
                        val cardUuid = rawValue.substring(dataLength - uuidLength, dataLength)
                        val apiUrl = rawValue.substring(0, dataLength - uuidLength)
                        Log.i("Adding to db",
                            "Got: name == $nameForNew, url == $apiUrl, uuid == $cardUuid")
                        CoroutineScope(Dispatchers.IO).launch {
                            updateDatabase(context, nameForNew, apiUrl, cardUuid, readingResult)
                        }
                    }
                }
                .addOnCanceledListener {
                    readingResult.result = ReadingResult.PossibleResults.CANCEL
                }
                .addOnFailureListener { e ->
                    Log.e("QR", e.message ?: "Empty exception msg")
                    readingResult.result = ReadingResult.PossibleResults.FAILURE
                }
        }

        suspend fun updateDatabase(context: Context,
                                   nameForNew: String, apiUrl: String, cardUuid: String,
                                   readingResult: ReadingResult) {
            val cardDao = CardsDatabase.getCardsDatabase(context).cardDao()
            val hasDuplicates = (cardDao.countCards(apiUrl, cardUuid) > 0)
            if (hasDuplicates) {
                Log.e("QR", "Got duplicate")
                readingResult.result = ReadingResult.PossibleResults.EXISTS
            }
            else {
                cardDao.insert(
                    Card(
                        name = nameForNew,
                        apiUrl = apiUrl,
                        cardUuid = cardUuid
                    )
                )
                readingResult.result = ReadingResult.PossibleResults.SUCCESS
            }
        }
    }
}
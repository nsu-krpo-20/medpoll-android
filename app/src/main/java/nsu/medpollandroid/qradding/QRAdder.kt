package nsu.medpollandroid.qradding

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import nsu.medpollandroid.data.Card
import nsu.medpollandroid.repositories.CardsDataRepository

class QRAdder {
    companion object {
        private const val uuidLength = 36 // 32 + 4 * '-'
        fun addFromQR(context: Context, nameForNew: String, existingCards: List<Card>,
                      error: MutableState<Boolean>, hasDuplicates: MutableState<Boolean>) {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = GmsBarcodeScanning.getClient(context, options)
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue: String? = barcode.rawValue
                    Log.d("QR adding", "Got: $rawValue")
                    if (rawValue == null) {
                        Log.e("QR", "Empty msg")
                        error.value = true
                    }
                    else {
                        val dataLength: Int = rawValue.length;
                        val cardUuid = rawValue.substring(dataLength - uuidLength, dataLength)
                        val apiUrl = rawValue.substring(0, dataLength - uuidLength)
                        Log.i("Adding card",
                            "Got: name == $nameForNew, url == $apiUrl, uuid == $cardUuid")
                        val card = Card(name = nameForNew, apiUrl = apiUrl, cardUuid = cardUuid)
                        if (hasDuplicates(existingCards, card)) {
                            Log.e("QR adding", "Got duplicate")
                            hasDuplicates.value = true
                        }
                        else {
                            val repository = CardsDataRepository.getInstance(context)
                            repository.insert(card)
                            Log.d("QR adding", "Success")
                        }
                    }
                }
                .addOnCanceledListener {}
                .addOnFailureListener { e ->
                    Log.e("QR", e.message ?: "Empty exception msg")
                    error.value = true
                }
        }

        private fun hasDuplicates(existingCards: List<Card>, newCard: Card): Boolean {
            for (card: Card in existingCards) {
                if ((card.cardUuid == newCard.cardUuid) && (card.apiUrl == newCard.apiUrl)) {
                    return true
                }
            }
            return false
        }
    }
}
package nsu.medpollandroid.qradding

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import nsu.medpollandroid.MedpollApplication
import nsu.medpollandroid.data.cards.Card

class QRAdder {
    companion object {
        private const val uuidLength = 36 // 32 + 4 * '-'
        private const val httpPrefix = "http://"
        private const val httpsPrefix = "https://"
        fun addFromQR(context: Context, nameForNew: String, existingCards: List<Card>,
                      error: MutableState<Boolean>, errorMsg: MutableState<String>,
                      hasDuplicates: MutableState<Boolean>) {
            val moduleInstall = ModuleInstall.getClient(context);
            val moduleInstallRequest = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(context))
                .setListener {
                    val errorString = "Scanner install in progress, please wait"
                    Log.e("QR", errorString)
                    error.value = true
                    errorMsg.value = errorString
                }
                .build()
            moduleInstall
                .installModules(moduleInstallRequest)
                .addOnSuccessListener {
                    if (it.areModulesAlreadyInstalled()) {
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
                                } else {

                                    val dataLength: Int = rawValue.length;
                                    val cardUuid =
                                        rawValue.substring(dataLength - uuidLength, dataLength)
                                    val apiUrlPart = rawValue.substring(0, dataLength - uuidLength)
                                    var apiUrl = apiUrlPart
                                    /*if (apiUrlPart.substring(0, httpsPrefix.length) != httpsPrefix) {
                                        var beginningIdx = 0
                                        if (apiUrlPart.substring(0, httpPrefix.length) == httpPrefix) {
                                            beginningIdx = httpPrefix.length
                                        }
                                        apiUrl = httpsPrefix + apiUrlPart.substring(beginningIdx, apiUrlPart.length)
                                    }*/
                                    if ('/' != apiUrl.get(apiUrl.length - 1)) {
                                        apiUrl += "/"
                                    }
                                    Log.i(
                                        "Adding card",
                                        "Got: name == $nameForNew, url == $apiUrl, uuid == $cardUuid"
                                    )
                                    val card = Card(
                                        name = nameForNew,
                                        apiUrl = apiUrl,
                                        cardUuid = cardUuid
                                    )
                                    if (hasDuplicates(existingCards, card)) {
                                        Log.e("QR adding", "Got duplicate")
                                        hasDuplicates.value = true
                                    } else {
                                        val application = context.applicationContext as MedpollApplication
                                        val repository = application.repositories.cardRepository
                                        repository.insertCard(card)
                                        Log.d("QR adding", "Success")
                                    }
                                }
                            }
                            .addOnCanceledListener {}
                            .addOnFailureListener { e ->
                                val errorString = e.message ?: "Empty exception msg"
                                Log.e("QR", errorString)
                                error.value = true
                                errorMsg.value = errorString
                            }
                    }
                }
                .addOnFailureListener {
                    val errorString = "Scanner failed to install"
                    Log.e("QR", errorString)
                    error.value = true
                    errorMsg.value = errorString
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
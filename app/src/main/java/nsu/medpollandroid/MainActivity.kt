package nsu.medpollandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nsu.medpollandroid.ui.CardsUI
import nsu.medpollandroid.ui.PrescriptionInfo
import nsu.medpollandroid.ui.PrescriptionsUI
import nsu.medpollandroid.ui.theme.MedpollTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityViewModel: ActivityViewModel by viewModels { ActivityViewModel.Factory }

        setContent {
            val cards = activityViewModel.cardsListStateFlow.collectAsStateWithLifecycle()
            val prescriptions = activityViewModel.prescriptionsInfoListStateFlow.collectAsStateWithLifecycle()
            val prescription = activityViewModel.prescriptionStateFlow.collectAsStateWithLifecycle()
            MedpollTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "cards") {
                    composable(
                        "cards"
                    ) {
                        val goToPrescriptionsFunc = { apiUrl: String, cardUuid: String ->
                            navController.navigate(
                                String.format("prescriptions/%s/%s", apiUrl, cardUuid)
                            )
                        }
                        CardsUI(cards, goToPrescriptionsFunc)
                    }
                    composable(
                        "prescription/{apiUrl}/{id}",
                        arguments = listOf(
                            navArgument("apiUrl") { type = NavType.StringType },
                            navArgument("id") { type = NavType.LongType }
                        )
                    ) {backStackEntry ->
                        val apiUrl = backStackEntry.arguments!!.getString("apiUrl")!!
                        val id = backStackEntry.arguments!!.getLong("id")
                        activityViewModel.readPrescriptionFor(apiUrl, id)
                        PrescriptionInfo(prescription)
                    }
                    composable(
                        "prescriptions/{apiUrl}/{cardUuid}",
                        arguments = listOf(
                            navArgument("apiUrl") { type = NavType.StringType },
                            navArgument("cardUuid") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val apiUrl = backStackEntry.arguments!!.getString("apiUrl")!!
                        val cardUuid = backStackEntry.arguments!!.getString("cardUuid")!!
                        val encodedUrl =
                            URLEncoder.encode(apiUrl, StandardCharsets.UTF_8.toString())
                        activityViewModel.updateLocalPrescriptionsListFor(apiUrl, cardUuid)
                        activityViewModel.readLocalPrescriptionsListFor(apiUrl, cardUuid)
                        val updatePrescriptionsListFunc = {
                            activityViewModel.updateLocalPrescriptionsListFor(apiUrl, cardUuid)
                        }
                        val goToPrescriptionFunc = { id: Long ->
                            navController.navigate(String.format("prescription/%s/%d", encodedUrl, id))
                        }
                        PrescriptionsUI(
                            prescriptions,
                            goToPrescriptionFunc,
                            updatePrescriptionsListFunc
                        )
                    }
                }
            }
        }
    }
}

package nsu.medpollandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.ui.CardsUI
import nsu.medpollandroid.ui.ErrorDialog
import nsu.medpollandroid.ui.PrescriptionsUI
import nsu.medpollandroid.ui.theme.MedpollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityViewModel: ActivityViewModel by viewModels { ActivityViewModel.Factory }
        val cards = mutableStateOf(emptyList<Card>())

        val prescriptions = activityViewModel.prescriptionsInfoListState

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.listStateFlow.collect { list ->
                    cards.value = list
                }
            }
        }

        setContent {
            MedpollTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "cards") {
                    composable(
                        "cards"
                    ) {
                        CardsUI(remember { cards }, navController)
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
                        val rememberedApiUrl = remember { mutableStateOf(apiUrl) }
                        val rememberedCardUuid = remember { mutableStateOf(cardUuid) }
                        val updateFailure = remember { mutableStateOf(false) }
                        val initialRequestSent = remember { mutableStateOf(false) }

                        if ((apiUrl != rememberedApiUrl.value) || (cardUuid != rememberedCardUuid.value)) {
                            rememberedApiUrl.value = apiUrl
                            rememberedCardUuid.value = cardUuid
                            initialRequestSent.value = false
                        }

                        if (updateFailure.value) {
                            ErrorDialog(updateFailure, stringResource(R.string.net_error_msg))
                        }
                        else {
                            val updatePrescriptionsListProvider =
                                object : UpdatePrescriptionsListProvider {
                                    override fun updatePrescriptionsList() {
                                        activityViewModel.updatePrescriptionsListFor(apiUrl, cardUuid, updateFailure)
                                    }
                                }
                            if (!initialRequestSent.value) {
                                updatePrescriptionsListProvider.updatePrescriptionsList()
                                initialRequestSent.value = true
                            }
                            PrescriptionsUI(
                                remember { prescriptions },
                                navController,
                                updatePrescriptionsListProvider
                            )
                        }
                    }
                }
            }
        }
    }

    interface UpdatePrescriptionsListProvider {
        fun updatePrescriptionsList()
    }
}

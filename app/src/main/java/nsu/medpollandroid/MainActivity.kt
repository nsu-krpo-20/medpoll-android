package nsu.medpollandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.ui.CardsUI
import nsu.medpollandroid.ui.PrescriptionsUI
import nsu.medpollandroid.ui.theme.MedpollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityViewModel: ActivityViewModel by viewModels { ActivityViewModel.Factory }
        val cards = mutableStateOf(emptyList<Card>())

        val prescriptions = mutableStateOf(emptyList<PrescriptionGeneralInfo>())

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
                        val apiUrl = backStackEntry.arguments!!.getString("apiUrl")
                        val cardUuid = backStackEntry.arguments!!.getString("cardUuid")
                        PrescriptionsUI(remember { prescriptions }, navController)
                    }
                }
            }
        }
    }
}

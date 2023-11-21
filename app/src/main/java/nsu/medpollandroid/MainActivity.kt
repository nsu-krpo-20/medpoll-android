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
import nsu.medpollandroid.data.Card
import nsu.medpollandroid.ui.CardsUI
import nsu.medpollandroid.ui.PrescriptionsUI
import nsu.medpollandroid.ui.theme.MedpollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardsViewModel: CardsViewModel by viewModels { CardsViewModel.Factory }
        val cards = mutableStateOf(emptyList<Card>())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardsViewModel.listStateFlow.collect { list ->
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
                        PrescriptionsUI(apiUrl = apiUrl!!, cardUuid = cardUuid!!,
                            navController = navController)
                    }
                }
            }
        }
    }
}

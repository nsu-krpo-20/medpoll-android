package nsu.medpollandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nsu.medpollandroid.db.Card
import nsu.medpollandroid.db.CardsDatabase
import nsu.medpollandroid.qrreading.QRReader
import nsu.medpollandroid.qrreading.ReadingResult
import nsu.medpollandroid.ui.theme.MedpollTheme
import org.jetbrains.annotations.Async

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedpollTheme {
                CardsUI()
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    private fun CardsUI() {
        val recomposeScope = currentRecomposeScope
        val openNameRequestDialog = remember { mutableStateOf(false) }
        val openErrorDialog = remember { mutableStateOf(false) }
        val openDuplicateDialog = remember { mutableStateOf(false) }
        val cards: List<Card> = emptyList()
        val values = remember { mutableStateOf(cards) }
        LaunchedEffect(Unit){// Refuses to work without LaunchedEffect. Says it straightforward.
            CoroutineScope(Dispatchers.IO).launch {
                values.value = requestCards()
            }
        }
        if (openNameRequestDialog.value) {
            NameRequestDialog(openNameRequestDialog, openErrorDialog, openDuplicateDialog, recomposeScope)
        }
        if (openErrorDialog.value) {
            ErrorDialog(openErrorDialog)
        }
        if (openDuplicateDialog.value) {
            DuplicateDialog(openDuplicateDialog)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar (
                    title = {
                        Text(stringResource(id = R.string.cards_title))
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(stringResource(id = R.string.add_card_button_text))
                    },
                    onClick = { openNameRequestDialog.value = true },
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.add_card_button_text)
                        )
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            backgroundColor = MaterialTheme.colors.background
        ) {
                /*
                listOf(
                    Card(0, "Невролог Ы.Ы. Ыев", "abc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea94"),
                    Card(1, "Стоматолог А.Ы. Ыыыыыыыыыыыыев", "bbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea95"),
                    Card(2, "Хирург Б.Ы. Ыев", "cbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea96"),
                    Card(0, "Невролог Ы.Ы. Ыев", "abc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea94"),
                    Card(1, "Стоматолог А.Ы. Ыыыыыыыыыыыыев", "bbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea95"),
                    Card(2, "Хирург Б.Ы. Ыев", "cbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea96"),
                    Card(0, "Невролог Ы.Ы. Ыев", "abc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea94"),
                    Card(1, "Стоматолог А.Ы. Ыыыыыыыыыыыыев", "bbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea95"),
                    Card(2, "Хирург Б.Ы. Ыев", "cbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea96"),
                    Card(0, "Невролог Ы.Ы. Ыев", "abc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea94"),
                    Card(1, "Стоматолог А.Ы. Ыыыыыыыыыыыыев", "bbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea95"),
                    Card(2, "Хирург Б.Ы. Ыев", "cbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea96"),
                    Card(0, "Невролог Ы.Ы. Ыев", "abc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea94"),
                    Card(1, "Стоматолог А.Ы. Ыыыыыыыыыыыыев", "bbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea95"),
                    Card(2, "Хирург Б.Ы. Ыев", "cbc.ru", "148b9149-a3a6-4802-abf0-bd62b501ea96")
                )
                */
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth() // In order to make list always scrollable by right finger
            ) {
                values.value.forEach { card ->
                    SingleCardUIElem(card = card)
                }
            }
        }
    }

    suspend fun requestCards(): List<Card> {
        return CardsDatabase.getCardsDatabase(applicationContext).cardDao().getAll()
    }

    @Composable
    private fun SingleCardUIElem(card: Card) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Button(
                onClick = {
                    /*
                    TODO
                    Send an Intent to start "Card Activity"?
                    */
                },
                modifier = Modifier
                    .weight(7f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = card.name,
                        fontSize = 24.sp
                    )
                    Text(
                        text = card.apiUrl,
                        fontSize = 8.sp
                    )
                    Text(
                        text = card.cardUuid,
                        fontSize = 8.sp
                    )
                }
            }
            Button(
                onClick = {
                    /*TODO*/
                },
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxHeight()
                    .weight(1f),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete_button_text)
                )
            }
        }
    }

    @Composable
    fun NameRequestDialog(openNameRequestDialog: MutableState<Boolean>,
                          openErrorDialog: MutableState<Boolean>,
                          openDuplicateDialog: MutableState<Boolean>,
                          recomposeScope: RecomposeScope) {
        val name = remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                openNameRequestDialog.value = false
            },
            title = {
                Text(
                    text = stringResource(id = R.string.name_request_dialog_header)
                )
            },
            text = {
                TextField(
                    value = name.value,
                    onValueChange = { newText ->
                        name.value = newText
                    },
                    singleLine = true
                )
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val readingResult = ReadingResult()
                            QRReader.read(applicationContext, name.value, readingResult)
                            if (readingResult.result == ReadingResult.PossibleResults.FAILURE) {
                                openErrorDialog.value = true
                            }
                            if (readingResult.result == ReadingResult.PossibleResults.EXISTS) {
                                openDuplicateDialog.value = true
                            }
                            openNameRequestDialog.value = false
                            recomposeScope.invalidate()
                        },
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok_button_text)
                        )
                    }
                    Button(
                        onClick = {
                            openNameRequestDialog.value = false
                        },
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel_button_text)
                        )
                    }
                }
            }
        )
    }

    @Composable
    fun ErrorDialog(openErrorDialog: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = {
                openErrorDialog.value = false
            },
            buttons = {
                Button(
                    onClick = {
                        openErrorDialog.value = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel_button_text)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.qr_error_msg_title)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.qr_error_msg_text)
                )
            }
        )
    }

    @Composable
    fun DuplicateDialog(openDuplicateDialog: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = {
                openDuplicateDialog.value = false
            },
            buttons = {
                Button(
                    onClick = {
                        openDuplicateDialog.value = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel_button_text)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.qr_dup_msg_title)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.qr_dup_msg_text)
                )
            }
        )
    }
}

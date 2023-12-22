package nsu.medpollandroid.ui

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nsu.medpollandroid.MedpollApplication
import nsu.medpollandroid.R
import nsu.medpollandroid.data.cards.Card
import nsu.medpollandroid.qradding.QRAdder
import nsu.medpollandroid.ui.previewproviders.SampleCardsPreviewProvider
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CardsUI(@PreviewParameter(SampleCardsPreviewProvider::class) cards: MutableState<List<Card>>,
            goToPrescriptions: (apiUrl: String, cardUuid: String) -> Unit = { _, _ ->  }) {
    val context = LocalContext.current
    val openNameRequestDialog = remember { mutableStateOf(false) }
    val openErrorDialog = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }
    val dupMsgString = stringResource(R.string.qr_dup_msg_text)
    val openDuplicateDialog = remember { mutableStateOf(false) }

    if (openNameRequestDialog.value) {
        NameRequestDialog(context, cards.value,
            openNameRequestDialog, openErrorDialog, errorMsg, openDuplicateDialog)
    }
    if (openErrorDialog.value) {
        ErrorDialog(openErrorDialog, errorMsg.value)
    }
    if (openDuplicateDialog.value) {
        ErrorDialog(openDuplicateDialog, dupMsgString)
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(it)
                .fillMaxWidth() // In order to make list always scrollable by right finger
        ) {
            items(cards.value) { card ->
                SingleCardUIElem(card, goToPrescriptions)
            }
        }
    }
}

@Composable
private fun SingleCardUIElem(card: Card,
                             goToPrescriptions: (apiUrl: String, cardUuid: String) -> Unit =
                             { _, _ -> }) {
    val application = LocalContext.current.applicationContext as MedpollApplication

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Button(
            onClick = {
                val encodedUrl =
                    URLEncoder.encode(card.apiUrl, StandardCharsets.UTF_8.toString())
                goToPrescriptions(encodedUrl, card.cardUuid)
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
                  application.repositories.cardRepository.delete(card)
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
private fun NameRequestDialog(context: Context,
                      existingCards: List<Card>,
                      openNameRequestDialog: MutableState<Boolean>,
                      openErrorDialog: MutableState<Boolean>,
                      errorMsg: MutableState<String>,
                      openDuplicateDialog: MutableState<Boolean>
) {
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
                        QRAdder.addFromQR(context, name.value, existingCards,
                            openErrorDialog, errorMsg, openDuplicateDialog)
                        openNameRequestDialog.value = false
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

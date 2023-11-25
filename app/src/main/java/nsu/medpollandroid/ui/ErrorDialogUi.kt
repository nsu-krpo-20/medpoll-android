package nsu.medpollandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nsu.medpollandroid.R

@Composable
fun ErrorDialog(openErrorDialog: MutableState<Boolean>, errorMsg: String) {
    AlertDialog(
        onDismissRequest = {
            openErrorDialog.value = false
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        openErrorDialog.value = false
                    },
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel_button_text)
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.error_msg_title)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = errorMsg
                )
            }
        }
    )
}

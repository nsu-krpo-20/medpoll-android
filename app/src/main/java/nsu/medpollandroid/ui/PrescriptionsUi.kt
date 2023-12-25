package nsu.medpollandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nsu.medpollandroid.R
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionsPreviewProvider

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrescriptionsUI(
    @PreviewParameter(SamplePrescriptionsPreviewProvider::class)
                prescriptions: State<List<PrescriptionEntity>>,
    goToPrescription: (id: Long) -> Unit = { _ ->  },
    updatePrescriptionsList: () -> Unit = { }) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar (
                title = {
                    Text(stringResource(id = R.string.prescriptions_title))
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(stringResource(id = R.string.update_prescriptions_button_text))
                },
                onClick = {
                    updatePrescriptionsList()
                },
                icon = {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = stringResource(id = R.string.update_prescriptions_button_text)
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        backgroundColor = MaterialTheme.colors.background
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
                .padding(it)
                .fillMaxWidth() // In order to make list always scrollable by right finger
        ) {
            items(prescriptions.value) { prescription ->
                SinglePrescriptionUIElem(prescription, goToPrescription)
            }
        }
    }
}

@Composable
private fun SinglePrescriptionUIElem(prescription: PrescriptionEntity,
                                     goToPrescription: (id: Long) -> Unit = { _ ->  }) {
    /*
    Do we need separate buttons for showing full information about prescription
    and filling a report or not?
    */
    Button(
        onClick = {
            goToPrescription(prescription.id)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format(stringResource(R.string.prescription_name_format),
                                    prescription.id + 1 /* To avoid frightening #0 */),
                fontSize = 24.sp
            )
            Text(
                text = String.format(stringResource(R.string.prescription_time_format),
                                    prescription.createdTime
                ),
                fontSize = 20.sp
            )
            Text(
                text = String.format(stringResource(R.string.prescription_author_format),
                    prescription.doctorFullName),
                fontSize = 20.sp
            )
            if (prescription.createdTime != prescription.editedTime) {
                Text(
                    text = String.format(stringResource(R.string.prescription_last_modified_format),
                        prescription.editedTime),
                    fontSize = 12.sp
                )
            }
        }
    }
}

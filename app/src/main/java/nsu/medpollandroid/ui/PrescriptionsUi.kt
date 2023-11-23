package nsu.medpollandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nsu.medpollandroid.R
import nsu.medpollandroid.data.PrescriptionGeneralInfo
import nsu.medpollandroid.ui.previewproviders.SampleCardsPreviewProvider
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionsPreviewProvider
import java.util.Date

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrescriptionsUI(
            @PreviewParameter(SamplePrescriptionsPreviewProvider::class)
                prescriptions: MutableState<List<PrescriptionGeneralInfo>>,
            //The only reason for navController's nullability is Studio's preview
            navController: NavController? = null) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar (
                title = {
                    Text(stringResource(id = R.string.prescriptions_title))
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth() // In order to make list always scrollable by right finger
        ) {
            prescriptions.value.forEach { prescription ->
                SinglePrescriptionUIElem(prescription, navController)
            }
        }
    }
}

@Composable
private fun SinglePrescriptionUIElem(prescription: PrescriptionGeneralInfo,
                                     navController: NavController?) {
    /*
    Do we need separate buttons for showing full information about prescription
    and filling a report or not?
    */
    Button(
        onClick = {
            // TODO(not implemented)
            // navController.navigate(...)
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
                                    prescription.createdTime * 1000
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
                        prescription.editedTime * 1000),
                    fontSize = 12.sp
                )
            }
        }
    }
}

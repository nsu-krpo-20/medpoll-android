package nsu.medpollandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nsu.medpollandroid.R
import nsu.medpollandroid.data.MedpollApi
import nsu.medpollandroid.data.ReportDataRequest
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.ui.theme.HospitalGreen
import nsu.medpollandroid.ui.theme.HospitalRed
import nsu.medpollandroid.utils.leftZeroPad
import nsu.medpollandroid.utils.periodValidOn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Date
@Composable
fun ReportForm(prescription: State<PrescriptionInfoData?>, onCompleteReportClick: (ReportDataRequest) -> Unit) {
    val data = prescription.value ?: return
    val medicineValid = data.medicines.map {medicine ->
        periodValidOn(medicine.period, data.creationTimestamp, Date())
    }.toList()
    val metricValid = data.metrics.map {metric ->
        periodValidOn(metric.period, data.creationTimestamp, Date())
    }.toList()
    var medicineClicked by rememberSaveable { mutableStateOf(MutableList(data.medicines.size) { _ -> false }) }
    var metricText by rememberSaveable { mutableStateOf(MutableList(data.metrics.size) { _ -> "" }) }
    var feedback by rememberSaveable {
        mutableStateOf("")
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.report_form_title))
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(stringResource(id = R.string.complete_report_button_text))
                    },
                    onClick = {
                      onCompleteReportClick(ReportDataRequest(
                          prescriptionId = data.id,
                          medsTaken = List(
                              medicineClicked.size
                          ) {i ->
                              if (!medicineValid[i]) null
                              else medicineClicked[i]
                          },
                          metrics = List(
                              metricText.size
                          ) {i ->
                              if (!metricValid[i]) null
                              else metricText[i]
                          },
                          feedback = feedback,
                          time = Date().time
                      ))
                    }, // TODO: send report and make report json datatype
                    icon = {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(id = R.string.complete_report_button_text)
                        )
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
                    .fillMaxWidth(),
            ) {
                PrimaryRow {
                    SecondaryText(text = stringResource(R.string.date), horizontalPadding = 8.dp)
                    val date = Date()
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.time = date
                    Row {
                        SecondaryText(
                            text = (calendar.get(Calendar.DAY_OF_MONTH)).leftZeroPad(2) +
                                    " / " +
                                    (calendar.get(Calendar.MONTH) + 1).leftZeroPad(2) +
                                    " / " +
                                    (calendar.get(Calendar.YEAR)).toString().leftZeroPad(4),
                            horizontalPadding = 8.dp,
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExpandingColumn(
                    text = stringResource(R.string.medicine),
                ) {
                    data.medicines.forEachIndexed { i, medicine ->
                        if (!medicineValid[i]) {
                            return@forEachIndexed
                        }
                        PrimaryRow(color = MaterialTheme.colors.secondary) {
                            Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                                SecondaryText(text = medicine.name, color = HospitalRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                SecondaryText(text = medicine.amount, color = HospitalRed)
                            }
                            Checkbox(checked = medicineClicked[i], onCheckedChange = {
                                val newMedicineClicked = medicineClicked.toMutableList()
                                newMedicineClicked[i] = !newMedicineClicked[i]
                                medicineClicked = newMedicineClicked
                            })
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExpandingColumn(
                    text = stringResource(R.string.metrics),
                ) {
                    data.metrics.forEachIndexed { i, metric ->
                        if (!metricValid[i]) {
                            return@forEachIndexed
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SecondaryText(text = metric.name, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = metricText[i],
                                onValueChange = {newText ->
                                    val newMetricText = metricText.toMutableList()
                                    newMetricText[i] = newText
                                    metricText = newMetricText
                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExpandingColumn(
                    text = stringResource(R.string.feedback),
                ) {
                    TextField(
                        value = feedback,
                        onValueChange = {newText ->
                            feedback = newText
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
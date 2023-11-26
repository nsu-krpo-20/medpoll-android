package nsu.medpollandroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import nsu.medpollandroid.R
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionInfoPreviewProvider
import nsu.medpollandroid.ui_data.Medicine
import nsu.medpollandroid.ui_data.Metric
import nsu.medpollandroid.ui_data.PrescriptionInfoData
import nsu.medpollandroid.ui_data.PrescriptionPeriod
import nsu.medpollandroid.ui_data.TimeOfDay
import java.text.SimpleDateFormat
import java.util.Date

private fun getDateTime(timestamp: Long): String? {
    val sdf = SimpleDateFormat("MM/dd/yyyy hh : mm")
    val netDate = Date(timestamp)
    return sdf.format(netDate)
}

fun String.mul(value: Int): String {
    if (value <= 0) {
        throw IllegalArgumentException("Until must be positive")
    }

    val builder = StringBuilder()
    for (i in 0..< value) {
        builder.append(this)
    }
    return builder.toString()
}
fun String.leftZeroPad(until: Int): String {
    if (until < 0) {
        throw IllegalArgumentException("Until must be positive")
    }

    if (until <= length) {
        return this
    }

    return "0".mul(until - length) + this
}
fun TimeOfDay.format(): String {
    return hours.toString().leftZeroPad(2) +
            " : " +
            minutes.toString().leftZeroPad(2)
}

@Composable
fun PeriodInfo(period: PrescriptionPeriod.Custom) {
    Text(period.description)
}

@Composable
fun PeriodInfo(caption: String, period: PrescriptionPeriod.NTimesDaily) {
    Column {
        Text(caption)
        Spacer(modifier = Modifier.height(2.dp))
        Column {
            period.timestamps.forEach { timestamp ->
                Column {
                    Text(timestamp.format())
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun PeriodInfo(period: PrescriptionPeriod.EachNDays) {
    Text("${period.period} раз в день в ${period.timestamp.format()}")
}

private val WEEKDAY_NAMES: Array<String> = arrayOf(
    "В понедельник",
    "Во вторник",
    "В среду",
    "В четверг",
    "В пятницу",
    "В субботу",
    "В воскресенье"
)
@Composable
fun PeriodInfo(period: PrescriptionPeriod.PerWeekday) {
    Column {
        period.weekdays.forEachIndexed { i, period ->
            period ?: return@forEachIndexed
            PeriodInfo(caption = "${WEEKDAY_NAMES[i]} в следующее время:", period = period)
        }
    }
}

@Composable
fun PeriodInfo(period: PrescriptionPeriod) {
    Column {
        Text("Период:")
        Spacer(modifier = Modifier.height(2.dp))
        when (period) {
            is PrescriptionPeriod.Custom -> PeriodInfo(period)
            is PrescriptionPeriod.EachNDays -> PeriodInfo(period)
            is PrescriptionPeriod.NTimesDaily -> PeriodInfo("Каждый день в следующее время:", period)
            is PrescriptionPeriod.PerWeekday -> PeriodInfo(period)
        }
    }
}
@Composable
fun MedicineInfo(medicine: Medicine) {
    Column {
        Text("${medicine.name} - ${medicine.amount}")
        Spacer(modifier = Modifier.height(2.dp))
        PeriodInfo(period = medicine.period)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrescriptionInfo(
    @PreviewParameter(SamplePrescriptionInfoPreviewProvider::class) data: PrescriptionInfoData
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar (
                    title = {
                        Text(stringResource(id = R.string.prescription_info_title))
                    }
                )
            }
        ) { padding ->
            Surface(modifier = Modifier.padding(padding)) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                        .fillMaxWidth(),
                ) {
                    Text("Создано в ${getDateTime(data.creationTimestamp)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Врач: ${data.doctorFullName}")
                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        data.medicines.forEach { medicine ->
                            MedicineInfo(medicine = medicine)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Column {
                        data.metrics.forEach { metric ->
                            MetricInfo(metric = metric)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricInfo(metric: Metric) {
    Column {
        Text(metric.name)
        Spacer(modifier = Modifier.height(2.dp))
        PeriodInfo(period = metric.period)
    }
}

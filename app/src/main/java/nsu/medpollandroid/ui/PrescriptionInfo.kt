package nsu.medpollandroid.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nsu.medpollandroid.R
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionInfoPreviewProvider
import nsu.medpollandroid.ui_data.Medicine
import nsu.medpollandroid.ui_data.Metric
import nsu.medpollandroid.ui_data.PrescriptionInfoData
import nsu.medpollandroid.ui_data.PrescriptionPeriod
import nsu.medpollandroid.utils.format
import nsu.medpollandroid.utils.leftZeroPad
import nsu.medpollandroid.utils.timesString
import java.util.Calendar
import java.util.Date

@Composable
fun PeriodInfo(period: PrescriptionPeriod.Custom) {
    Text(period.description)
}

@Composable
fun PeriodInfo(caption: String, period: PrescriptionPeriod.NTimesDaily) {
    Row {
        Text(caption)
        Spacer(modifier = Modifier.width(2.dp))
        period.timestamps.forEachIndexed { i, timestamp ->
            Text(timestamp.format() + if (i == period.timestamps.size - 1) "" else ",")
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
fun PeriodInfo(period: PrescriptionPeriod.EachNDays) {
    Text(
        stringResource(
            R.string.per_day_in,
            period.period,
            period.period.timesString(),
            period.timestamp.format()
        ))
}
@Composable
fun PeriodInfo(period: PrescriptionPeriod.PerWeekday) {
    val weekdayNames: Array<String> = arrayOf(
        stringResource(R.string.on_mondays),
        stringResource(R.string.on_tuesdays),
        stringResource(R.string.on_wednesdays),
        stringResource(R.string.on_thursdays),
        stringResource(R.string.on_fridays),
        stringResource(R.string.on_saturdays),
        stringResource(R.string.on_sundays)
    )

    Column {
        period.weekdays.forEachIndexed { i, period ->
            period ?: return@forEachIndexed
            PeriodInfo(caption = stringResource(R.string.on_weekday_at_the_time, weekdayNames[i]), period = period)
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
            is PrescriptionPeriod.NTimesDaily -> PeriodInfo(stringResource(R.string.each_day_at_the_time), period)
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

@Composable
fun SecondaryText(
    text: String,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    color: Color = MaterialTheme.colors.secondary
) {
    Text(
        text,
        color = Color.White,
        modifier = modifier
            .background(color = color)
            .padding(horizontalPadding, 4.dp)
            .border(BorderStroke(0.dp, Color.Transparent), shape = CircleShape),
        textAlign = TextAlign.Center
    )
}

@Composable
fun PrimaryRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colors.primary)
            .padding(8.dp)
            .border(BorderStroke(0.dp, Color.Transparent))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = content
    )
}

@Composable
fun ExpandingColumn(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var hidden by rememberSaveable { mutableStateOf(false) }

    val newModifier = if (hidden) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = MaterialTheme.colors.primary)
    }

    val color = if (hidden) {MaterialTheme.colors.primary} else {MaterialTheme.colors.secondary}
    Column(modifier = newModifier) {
        Button(
            onClick = { hidden = !hidden },
            shape = RectangleShape,
            elevation = null,
            contentPadding = PaddingValues(0.dp)
        ) {
            SecondaryText(text = text, color = color, modifier = Modifier.fillMaxWidth())
        }
        if (hidden) {
            return@Column
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
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
                    PrimaryRow {
                        SecondaryText(text = stringResource(R.string.created), horizontalPadding = 8.dp)
                        val date = Date(data.creationTimestamp)
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.time = date
                        Row {
                            SecondaryText(
                                text = (calendar.get(Calendar.MONTH) + 1).leftZeroPad(2) +
                                        " / " +
                                        (calendar.get(Calendar.DAY_OF_MONTH)).leftZeroPad(2) +
                                        " / " +
                                        (calendar.get(Calendar.YEAR)).toString().leftZeroPad(4),
                                horizontalPadding = 8.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            SecondaryText(
                                text = (calendar.get(Calendar.HOUR)).toString().leftZeroPad(2) +
                                        " : " +
                                        (calendar.get(Calendar.MINUTE)).toString().leftZeroPad(2),
                                horizontalPadding = 8.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryRow {
                        SecondaryText(text = stringResource(R.string.doctor))
                        Spacer(Modifier.width(12.dp))
                        SecondaryText(text = data.doctorFullName)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExpandingColumn(text = stringResource(R.string.medicine)) {
                        data.medicines.forEach { medicine ->
                            MedicineInfo(medicine = medicine)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExpandingColumn(text = stringResource(R.string.metrics)) {
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

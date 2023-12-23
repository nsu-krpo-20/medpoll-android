package nsu.medpollandroid.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nsu.medpollandroid.R
import nsu.medpollandroid.data.prescriptions.Medicine
import nsu.medpollandroid.data.prescriptions.Metric
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.PrescriptionPeriod
import nsu.medpollandroid.ui.previewproviders.SamplePrescriptionInfoPreviewProvider
import nsu.medpollandroid.ui.theme.HospitalRed
import nsu.medpollandroid.utils.daysString
import nsu.medpollandroid.utils.format
import nsu.medpollandroid.utils.leftZeroPad
import java.util.Calendar
import java.util.Date

@Composable
fun PeriodInfo(period: PrescriptionPeriod.Custom) {
    PrimaryRow(color = MaterialTheme.colors.secondary, topPadding = 0.dp) {
        SecondaryText(period.description, modifier = Modifier.fillMaxWidth(), color = HospitalRed)
    }
}

@Composable
fun PeriodInfo(caption: String, period: PrescriptionPeriod.NTimesDaily) {
    PrimaryRow(color = Color.Transparent, horizontalArrangement = Arrangement.Center) {
        SecondaryText(caption, color = HospitalRed)
        Spacer(modifier = Modifier.width(8.dp))
        period.timestamps.forEach { timestamp ->
            SecondaryText(timestamp.format(), color = HospitalRed)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun PeriodInfo(period: PrescriptionPeriod.EachNDays) {
    //TODO("Fix and change to раз в два дня")
    PrimaryRow(color = MaterialTheme.colors.secondary, topPadding = 0.dp) {
        SecondaryText(stringResource(
            R.string.per_day_in,
            period.period,
            period.period.daysString(),
            period.timestamp.format()
        ), modifier = Modifier.fillMaxWidth(), color = HospitalRed)
    }
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
    when (period) {
        is PrescriptionPeriod.Custom -> PeriodInfo(period)
        is PrescriptionPeriod.EachNDays -> PeriodInfo(period)
        is PrescriptionPeriod.NTimesDaily -> PeriodInfo(stringResource(R.string.each_day_at_the_time), period)
        is PrescriptionPeriod.PerWeekday -> PeriodInfo(period)
    }
}
@Composable
fun MedicineInfo(medicine: Medicine) {
    var hidden by rememberSaveable { mutableStateOf(true) }

    Column {
        Button(onClick = {hidden = !hidden}, elevation = null, shape = RectangleShape, contentPadding = PaddingValues(0.dp)) {
            Column(modifier = Modifier.background(MaterialTheme.colors.secondary)) {
                PrimaryRow(color = Color.Transparent) {
                    SecondaryText(text = medicine.name, color = HospitalRed)
                    SecondaryText(text = medicine.amount, color = HospitalRed)
                }

                if (hidden) { return@Column }
                PeriodInfo(period = medicine.period)
            }
        }
    }
}

@Composable
fun SecondaryText(
    text: String,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    color: Color = MaterialTheme.colors.secondary,
    shape: Shape = RectangleShape
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .background(color = color)
            .padding(horizontalPadding, 4.dp)
            .border(BorderStroke(0.dp, Color.Transparent), shape = shape),
    ) {
        Text(
            text,
            modifier = modifier
                .background(color = color),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrimaryRow(modifier: Modifier = Modifier, horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceBetween, horizontalPadding: Dp = 8.dp, topPadding: Dp = 8.dp, bottomPadding: Dp = 8.dp, color: Color = MaterialTheme.colors.primary, content: @Composable RowScope.() -> Unit) {
    FlowRow(
        modifier = modifier
            .background(color = color)
            .padding(horizontalPadding, topPadding, horizontalPadding, bottomPadding)
            .fillMaxWidth()
        ,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
fun ExpandingColumn(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var hidden by rememberSaveable { mutableStateOf(true) }

    val newModifier = if (hidden) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .padding(10.dp, 4.dp)
    }

    val color = if (hidden) {MaterialTheme.colors.primary} else {MaterialTheme.colors.secondary}
    Column(
        if (hidden) {
            Modifier
                .background(color = MaterialTheme.colors.primary)
                .padding(0.dp)
        } else {
            Modifier
                .background(color = MaterialTheme.colors.primary)
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = { hidden = !hidden },
            shape = RectangleShape,
            elevation = null,
            contentPadding = PaddingValues(10.dp, 4.dp),
        ) {
            Text(
                text,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = color)
                    .padding(0.dp, 8.dp)
                    .border(BorderStroke(0.dp, Color.Transparent), shape = CircleShape),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        if (hidden) {
            return@Column
        }
        Column(modifier = newModifier) {
            content()
        }
    }
}

/*
@Composable
fun PrescriptionInfo(id: Long) {
    val application = LocalContext.current.applicationContext as MedpollApplication
    var data by remember {
        mutableStateOf<PrescriptionInfoData?>(null)
    }

    LaunchedEffect(id) {
        application.repositories.prescriptionRepository.getPrescription(id).collect { newData ->
            data = newData
        }
    }

    if (data == null) return
    PrescriptionInfo(data!!)
}
*/

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrescriptionInfo(
    @PreviewParameter(SamplePrescriptionInfoPreviewProvider::class)
        prescriptionState: State<PrescriptionInfoData?>
) {
    val data = prescriptionState.value ?: return
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
                            text = (calendar.get(Calendar.DAY_OF_MONTH)).leftZeroPad(2) +
                                    " / " +
                                    (calendar.get(Calendar.MONTH) + 1).leftZeroPad(2) +
                                    " / " +
                                    (calendar.get(Calendar.YEAR)).toString().leftZeroPad(4),
                            horizontalPadding = 8.dp,
                            shape = CircleShape
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SecondaryText(
                            text = (calendar.get(Calendar.HOUR)).toString().leftZeroPad(2) +
                                    " : " +
                                    (calendar.get(Calendar.MINUTE)).toString().leftZeroPad(2),
                            horizontalPadding = 8.dp,
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryRow {
                    data.doctorFullName.split(" ").forEach {
                        SecondaryText(text = it)
                        Spacer(Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExpandingColumn(
                    text = stringResource(R.string.medicine),
                ) {
                    data.medicines.forEach { medicine ->
                        MedicineInfo(medicine = medicine)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExpandingColumn(
                    text = stringResource(R.string.metrics),
                ) {
                    data.metrics.forEach { metric ->
                        MetricInfo(metric = metric)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MetricInfo(metric: Metric) {
    var hidden by rememberSaveable { mutableStateOf(true) }

    Column {
        Button(onClick = {hidden = !hidden}, elevation = null, shape = RectangleShape, contentPadding = PaddingValues(0.dp)) {
            Column(modifier = Modifier.background(MaterialTheme.colors.secondary)) {
                PrimaryRow(color = Color.Transparent, horizontalArrangement = Arrangement.Center) {
                    SecondaryText(text = metric.name, color = HospitalRed)
                }

                if (hidden) { return@Column }
                PeriodInfo(period = metric.period)
            }
        }
    }
}

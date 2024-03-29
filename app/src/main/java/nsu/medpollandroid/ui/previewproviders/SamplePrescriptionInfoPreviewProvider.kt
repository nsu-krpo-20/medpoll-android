package nsu.medpollandroid.ui.previewproviders

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nsu.medpollandroid.data.prescriptions.Custom
import nsu.medpollandroid.data.prescriptions.EachNDays
import nsu.medpollandroid.data.prescriptions.Medicine
import nsu.medpollandroid.data.prescriptions.Metric
import nsu.medpollandroid.data.prescriptions.NTimesDaily
import nsu.medpollandroid.data.prescriptions.PerWeekday
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.PrescriptionPeriod
import nsu.medpollandroid.data.prescriptions.TimeOfDay
import nsu.medpollandroid.utils.listOfWeekdays

class SamplePrescriptionInfoPreviewProvider: PreviewParameterProvider<PrescriptionInfoData> {
    override val values: Sequence<PrescriptionInfoData>
        get() = sequenceOf(
            PrescriptionInfoData(
                id = 0,
                isActive = true,
                creationTimestamp = 1_697_356_800000,
                doctorFullName = "Пирогов Николай Иванович",
                medicines = listOf(
                    Medicine(
                        id = 0,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = NTimesDaily(
                            listOf(
                                TimeOfDay(8, 0),
                                TimeOfDay(21, 0)
                            )
                        )
                    ),
                    Medicine(
                        id = 1,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = EachNDays(
                            period = 2,
                            timestamp = TimeOfDay(8, 0)
                        )
                    ),
                    Medicine(
                        id = 2,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = PerWeekday(
                            listOfWeekdays(
                                monday = NTimesDaily(
                                    listOf(
                                        TimeOfDay(8, 0),
                                        TimeOfDay(21, 0)
                                    )
                                ),
                                friday = NTimesDaily(
                                    listOf(
                                        TimeOfDay(11, 0),
                                        TimeOfDay(21, 0)
                                    )
                                )
                            )
                        )
                    ),
                    Medicine(
                        id = 3,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = Custom(description = "Когда захотите")
                    )
                ),
                metrics = listOf(
                    Metric(
                        id = 0,
                        name = "Давление",
                        period = NTimesDaily(
                            listOf(
                                TimeOfDay(8, 0),
                                TimeOfDay(21, 0)
                            )
                        )
                    ),
                    Metric(
                        id = 1,
                        name = "Давление",
                        period = EachNDays(
                            period = 2,
                            timestamp = TimeOfDay(8, 0)
                        )
                    ),
                    Metric(
                        id = 2,
                        name = "Давление",
                        period = PerWeekday(
                            listOfWeekdays(
                                monday = NTimesDaily(
                                    listOf(
                                        TimeOfDay(8, 0),
                                        TimeOfDay(21, 0)
                                    )
                                ),
                                friday = NTimesDaily(
                                    listOf(
                                        TimeOfDay(11, 0),
                                        TimeOfDay(21, 0)
                                    )
                                )
                            )
                        )
                    ),
                    Metric(
                        id = 3,
                        name = "Давление",
                        period = Custom(description = "Когда захотите")
                    )
                ),
            )
        )
}
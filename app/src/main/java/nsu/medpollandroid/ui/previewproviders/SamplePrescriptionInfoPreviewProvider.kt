package nsu.medpollandroid.ui.previewproviders

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nsu.medpollandroid.data.prescriptions.Medicine
import nsu.medpollandroid.data.prescriptions.Metric
import nsu.medpollandroid.data.prescriptions.PrescriptionInfoData
import nsu.medpollandroid.data.prescriptions.PrescriptionPeriod
import nsu.medpollandroid.data.prescriptions.TimeOfDay
import nsu.medpollandroid.utils.listOfWeekdays

class SamplePrescriptionInfoPreviewProvider: PreviewParameterProvider<PrescriptionInfoData> {
    override val values: Sequence<PrescriptionInfoData>
        get() = sequenceOf(
            PrescriptionInfoData(
                creationTimestamp = 1_697_356_800,
                doctorFullName = "Пирогов Николай Иванович",
                medicines = listOf(
                    Medicine(
                        id = 0,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = PrescriptionPeriod.NTimesDaily(
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
                        period = PrescriptionPeriod.EachNDays(
                            period = 2,
                            timestamp = TimeOfDay(8, 0)
                        )
                    ),
                    Medicine(
                        id = 2,
                        name = "Захрапин",
                        amount = "200 мг",
                        period = PrescriptionPeriod.PerWeekday(
                            listOfWeekdays(
                                monday = PrescriptionPeriod.NTimesDaily(
                                    listOf(
                                        TimeOfDay(8, 0),
                                        TimeOfDay(21, 0)
                                    )
                                ),
                                friday = PrescriptionPeriod.NTimesDaily(
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
                        period = PrescriptionPeriod.Custom(description = "Когда захотите")
                    )
                ),
                metrics = listOf(
                    Metric(
                        id = 0,
                        name = "Давление",
                        period = PrescriptionPeriod.NTimesDaily(
                            listOf(
                                TimeOfDay(8, 0),
                                TimeOfDay(21, 0)
                            )
                        )
                    ),
                    Metric(
                        id = 1,
                        name = "Давление",
                        period = PrescriptionPeriod.EachNDays(
                            period = 2,
                            timestamp = TimeOfDay(8, 0)
                        )
                    ),
                    Metric(
                        id = 2,
                        name = "Давление",
                        period = PrescriptionPeriod.PerWeekday(
                            listOfWeekdays(
                                monday = PrescriptionPeriod.NTimesDaily(
                                    listOf(
                                        TimeOfDay(8, 0),
                                        TimeOfDay(21, 0)
                                    )
                                ),
                                friday = PrescriptionPeriod.NTimesDaily(
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
                        period = PrescriptionPeriod.Custom(description = "Когда захотите")
                    )
                ),
            )
        )
}
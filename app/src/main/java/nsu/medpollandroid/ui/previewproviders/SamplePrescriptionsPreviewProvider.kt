package nsu.medpollandroid.ui.previewproviders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nsu.medpollandroid.data.prescriptions.db.PrescriptionEntity

class SamplePrescriptionsPreviewProvider : PreviewParameterProvider<MutableState<List<PrescriptionEntity>>> {
    override val values: Sequence<MutableState<List<PrescriptionEntity>>>
        get() = sequenceOf(mutableStateOf(listOf(
            PrescriptionEntity(
                0,
                "abc.ru",
                1,
                1697360400000,
                1697360400000,
                "Пирогов Николай Иванович",
                true
            ),
            PrescriptionEntity(
                1,
                "bbc.ru",
                1,
                1697360400000,
                1697367600000,
                "Мешалкин Евгений Николаевич",
                true
            )
        )))
}
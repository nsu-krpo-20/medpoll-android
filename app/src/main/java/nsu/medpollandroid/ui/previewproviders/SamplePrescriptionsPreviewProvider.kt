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
                1,
                1697360400,
                1697360400,
                "Пирогов Николай Иванович",
                true
            ),
            PrescriptionEntity(
                1,
                1,
                1697360400,
                1697367600,
                "Мешалкин Евгений Николаевич",
                true
            )
        )))
}
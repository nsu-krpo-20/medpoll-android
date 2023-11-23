package nsu.medpollandroid.ui.previewproviders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nsu.medpollandroid.data.PrescriptionGeneralInfo

class SamplePrescriptionsPreviewProvider : PreviewParameterProvider<MutableState<List<PrescriptionGeneralInfo>>> {
    override val values: Sequence<MutableState<List<PrescriptionGeneralInfo>>>
        get() = sequenceOf(mutableStateOf(listOf(
            PrescriptionGeneralInfo(
                0,
                1,
                1697360400,
                1697360400,
                "Пирогов Николай Иванович",
                true
            ),
            PrescriptionGeneralInfo(
                1,
                1,
                1697360400,
                1697367600,
                "Мешалкин Евгений Николаевич",
                true
            )
        )))
}
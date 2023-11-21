package nsu.medpollandroid.ui.previewproviders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nsu.medpollandroid.data.Card

class SampleCardsPreviewProvider : PreviewParameterProvider<MutableState<List<Card>>> {
    override val values: Sequence<MutableState<List<Card>>>
        get() = sequenceOf(mutableStateOf(listOf(
            Card(0, "Невролог А.А. Петров", "abc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea94"),
            Card(1, "Стоматолог Б.А. Длиииннофамилиев", "bbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea95"),
            Card(2, "Хирург В.А. Петров", "cbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea96"),
            Card(3, "Невролог А.А. Петров", "abc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea97"),
            Card(4, "Стоматолог Б.А. Длиииннофамилиев", "bbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea98"),
            Card(5, "Хирург В.А. Петров", "cbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea99"),
            Card(6, "Невролог А.А. Петров", "abc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea00"),
            Card(7, "Стоматолог Б.А. Длиииннофамилиев", "bbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea01"),
            Card(8, "Хирург В.А. Петров", "cbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea02"),
            Card(9, "Невролог А.А. Петров", "abc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea03"),
            Card(10, "Стоматолог Б.А. Длиииннофамилиев", "bbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea04"),
            Card(11, "Хирург В.А. Петров", "cbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea05"),
            Card(12, "Невролог А.А. Петров", "abc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea06"),
            Card(13, "Стоматолог Б.А. Длиииннофамилиев", "bbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea07"),
            Card(14, "Хирург В.А. Петров", "cbc.ru",
                "148b9149-a3a6-4802-abf0-bd62b501ea08")
        )))
}
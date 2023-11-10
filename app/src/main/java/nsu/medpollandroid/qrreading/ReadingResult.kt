package nsu.medpollandroid.qrreading

class ReadingResult {
    var result: PossibleResults = PossibleResults.SUCCESS

    enum class PossibleResults {
        SUCCESS, CANCEL, FAILURE, EXISTS
    }
}

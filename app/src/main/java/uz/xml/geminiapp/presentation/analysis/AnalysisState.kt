package uz.xml.geminiapp.presentation.analysis

sealed class AnalysisState {
    data class Loading(val loadingText: String = "Loading data from Gemini...") : AnalysisState()
    data class Success(val result: String) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
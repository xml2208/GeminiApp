package uz.xml.geminiapp.presentation.analyze

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.presentation.camera.AppLanguage

class AnalyzeViewModel(
    private val repository: GeminiRepository,
) : ViewModel() {

    var analysisState by mutableStateOf<AnalysisState>(AnalysisState.Loading())
        private set

    fun analyzeImage(
        context: Context,
        imageUri: Uri,
        promptType: GeminiPrompt,
        appLanguage: AppLanguage,
    ) {
        analysisState = AnalysisState.Loading()
        viewModelScope.launch {

            try {
                val bitmap = context.contentResolver.openInputStream(imageUri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: throw IllegalArgumentException("Bitmap is null")

                val resultText = repository.analyzeFood(bitmap = bitmap, promptType = promptType, language = appLanguage)
                analysisState = AnalysisState.Success(resultText)

            } catch (e: Exception) {
                analysisState = AnalysisState.Error("Analysis failed: ${e.message}")
            }
        }
    }
}

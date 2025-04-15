package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.domain.usecase.GetSelectedLanguageUseCase
import uz.xml.geminiapp.presentation.language.AppLanguage

class DailyCaloriesViewModel(
    private val geminiRepository: GeminiRepository,
    getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
) : ViewModel() {

    var uiState = mutableStateOf("")
        private set

    private val currentLanguage = getSelectedLanguageUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppLanguage.ENGLISH
        ).value

    fun estimateCalories(
        gender: String,
        age: Int,
        height: Int,
        weight: Int,
        activityLevel: String,
        goal: String,
    ) {
        viewModelScope.launch {
            try {
                val result = geminiRepository.estimateDailyCalories(
                    gender = gender,
                    age = age,
                    heightCm = height,
                    weightKg = weight,
                    activityLevel = activityLevel,
                    goal = goal,
                    language = currentLanguage
                )
                uiState.value = result
            } catch (e: Exception) {
                uiState.value = "Error: ${e.message}"
            }
        }
    }
}
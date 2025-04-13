package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.xml.geminiapp.domain.repository.GeminiRepository

class DailyCaloriesViewModel(
    private val geminiRepository: GeminiRepository
): ViewModel() {

    var uiState = mutableStateOf("")
        private set

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
                    gender, age, height, weight, activityLevel, goal
                )
                uiState.value = result
            } catch (e: Exception) {
                uiState.value = "Error: ${e.message}"
            }
        }
    }
}
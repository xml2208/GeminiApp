package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.runtime.State
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

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _formState = mutableStateOf(CalorieFormState("", "", ""))
    val formState: State<CalorieFormState> = _formState

    private val _showAge = mutableStateOf(false)
    val showAge: State<Boolean> = _showAge

    private val _showHeight = mutableStateOf(false)
    val showHeight: State<Boolean> = _showHeight

    private val _showWeight = mutableStateOf(false)
    val showWeight: State<Boolean> = _showWeight

    private val _showActivity = mutableStateOf(false)
    val showActivity: State<Boolean> = _showActivity

    private val _showGoal = mutableStateOf(false)
    val showGoal: State<Boolean> = _showGoal

    private val _showCalculateButton = mutableStateOf(false)
    val showCalculateButton: State<Boolean> = _showCalculateButton

    private val currentLanguage = getSelectedLanguageUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppLanguage.ENGLISH
        ).value

    fun updateFormState(newState: CalorieFormState) {
        _formState.value = newState
        updateVisibilityStates(newState)
    }

    private fun updateVisibilityStates(state: CalorieFormState) {
        _showAge.value = state.gender.isNotEmpty()
        _showHeight.value = _showAge.value && state.age.isNotBlank()
        _showWeight.value = _showHeight.value && state.height.isNotBlank()
        _showActivity.value = _showWeight.value && state.weight.isNotBlank()
        _showGoal.value = _showActivity.value && state.activity.isNotBlank()
        _showCalculateButton.value = _showGoal.value && state.goal.isNotBlank()
    }

    fun initializeOptions(options: CalorieScreenOptions) {
        if (_formState.value.gender.isEmpty()) {
            _formState.value = _formState.value.copy(
                gender = options.genderOptions.firstOrNull().orEmpty(),
                activity = options.activityOptions.getOrNull(2).orEmpty(),
                goal = options.goalOptions.firstOrNull().orEmpty()
            )
            updateVisibilityStates(_formState.value)
        }
    }

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
                _isLoading.value = true
                val result = geminiRepository.estimateDailyCalories(
                    gender = gender,
                    age = age,
                    heightCm = height,
                    weightKg = weight,
                    activityLevel = activityLevel,
                    goal = goal,
                    language = currentLanguage
                )
                _isLoading.value = false
                uiState.value = result
            } catch (e: Exception) {
                _isLoading.value = true
                uiState.value = "Error: ${e.message}"
            }
        }
    }
}
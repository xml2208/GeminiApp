package uz.xml.geminiapp.presentation.meal_plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.xml.geminiapp.data.model.MealPlanRequest
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.domain.repository.UserPreferencesRepository
import uz.xml.geminiapp.domain.usecase.GetSelectedLanguageUseCase
import uz.xml.geminiapp.presentation.language.AppLanguage

class MealPlanViewModel(
    private val geminiRepository: GeminiRepository,
    userPreferencesRepository: UserPreferencesRepository,
    getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
) : ViewModel() {

    private val _mealsPerDay = MutableStateFlow(0)
    val mealsPerDay: StateFlow<Int> = _mealsPerDay.asStateFlow()

    private val _allergies = MutableStateFlow("")
    val allergies: StateFlow<String> = _allergies.asStateFlow()

    private val _likesDislikes = MutableStateFlow("")
    val likesDislikes: StateFlow<String> = _likesDislikes.asStateFlow()

    private val _dietType = MutableStateFlow(DietType.NONE.value)
    val dietType: StateFlow<String> = _dietType.asStateFlow()

    private val _activityLevel = MutableStateFlow(ActivityLevel.MEDIUM.value)
    val activityLevel: StateFlow<String> = _activityLevel.asStateFlow()

    private val _goal = MutableStateFlow(Goal.MAINTAIN.value)
    val goal: StateFlow<String> = _goal.asStateFlow()

    private val _cuisineType = MutableStateFlow("")
    val cuisineType: StateFlow<String> = _cuisineType.asStateFlow()

    private val _calorieInput = MutableStateFlow("")
    val calorieInput: StateFlow<String> = _calorieInput.asStateFlow()

    private val _uiState = MutableStateFlow<MealPlanUiState>(MealPlanUiState.Input)
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    private val _showValidationErrors = MutableStateFlow(false)

    val storedCalories = userPreferencesRepository.caloriesFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    private val currentLanguage = getSelectedLanguageUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppLanguage.ENGLISH
        )

    val hasStoredCalories: StateFlow<Boolean> = storedCalories
        .map { calories -> calories.isNotBlank() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    private val _manualCalorieSelection = MutableStateFlow<Boolean?>(null)

    val effectiveUseStoredCalories: StateFlow<Boolean> = combine(
        hasStoredCalories, _manualCalorieSelection
    ) { hasStored, manualSelection -> manualSelection ?: hasStored }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    private val dailyCalories: StateFlow<Int> = combine(
        storedCalories,
        _calorieInput,
        effectiveUseStoredCalories
    ) { stored, input, useStored ->
        if (useStored && stored.isNotBlank()) {
            stored.toIntOrNull() ?: 0
        } else {
            input.toIntOrNull() ?: 0
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val hasAllergiesError: StateFlow<Boolean> = combine(
        _allergies,
        _showValidationErrors
    ) { allergies, showErrors ->
        showErrors && allergies.isBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasLikesDislikesError: StateFlow<Boolean> = combine(
        _likesDislikes,
        _showValidationErrors
    ) { likesDislikes, showErrors ->
        showErrors && likesDislikes.isBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasCuisineTypeError: StateFlow<Boolean> = combine(
        _cuisineType,
        _showValidationErrors
    ) { cuisineType, showErrors ->
        showErrors && cuisineType.isBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasCalorieError: StateFlow<Boolean> = combine(
        effectiveUseStoredCalories,
        _calorieInput,
        dailyCalories,
        _showValidationErrors
    ) { useStored, input, calories, showErrors ->
        if (!showErrors) return@combine false

        if (!useStored) {
            input.isBlank() || input.toIntOrNull() == null || input.toIntOrNull()
                ?.let { it <= 0 } == true
        } else {
            calories <= 0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val isInternalFormValid: StateFlow<Boolean> = combine(
        _allergies,
        _likesDislikes,
        _cuisineType,
        dailyCalories,
        _calorieInput
    ) { allergies, likesDislikes, cuisineType, calories, input ->
        allergies.isNotBlank() &&
                likesDislikes.isNotBlank() &&
                cuisineType.isNotBlank() &&
                (calories > 0 || (input.isNotBlank() && input.toIntOrNull() != null && input.toInt() > 0))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateMealsPerDay(value: Int) {
        _mealsPerDay.value = value
    }

    fun updateAllergies(value: String) {
        _allergies.value = value
    }

    fun updateLikesDislikes(value: String) {
        _likesDislikes.value = value
    }

    fun updateDietType(value: String) {
        _dietType.value = value
    }

    fun updateActivityLevel(value: String) {
        _activityLevel.value = value
    }

    fun updateGoal(value: String) {
        _goal.value = value
    }

    fun updateCuisineType(value: String) {
        _cuisineType.value = value
    }

    fun updateCalorieInput(value: String) {
        _calorieInput.value = value
    }

    fun toggleCalorieSource(useStored: Boolean) {
        _manualCalorieSelection.value = useStored
    }

    fun generateMealPlan() {
        _showValidationErrors.value = true

        if (!isInternalFormValid.value) return

        val calories = dailyCalories.value
        if (calories <= 0) return

        viewModelScope.launch {
            _uiState.value = MealPlanUiState.Loading
            try {
                val request = MealPlanRequest(
                    dailyCalories = calories,
                    mealsPerDay = _mealsPerDay.value,
                    allergies = _allergies.value,
                    likesDislikes = _likesDislikes.value,
                    dietType = _dietType.value,
                    activityLevel = _activityLevel.value,
                    cuisineType = _cuisineType.value,
                    goal = _goal.value,
                )

                val result = geminiRepository.generateMealPlan(request, currentLanguage.value)
                _uiState.value = MealPlanUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = MealPlanUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetToInput() {
        _uiState.value = MealPlanUiState.Input
        _showValidationErrors.value = false
    }

    fun resetForm() {
        _mealsPerDay.value = 0
        _allergies.value = ""
        _likesDislikes.value = ""
        _dietType.value = DietType.NONE.value
        _activityLevel.value = ActivityLevel.MEDIUM.value
        _goal.value = Goal.MAINTAIN.value
        _cuisineType.value = ""
        _calorieInput.value = ""
        _showValidationErrors.value = false
        _manualCalorieSelection.value = null
    }
}
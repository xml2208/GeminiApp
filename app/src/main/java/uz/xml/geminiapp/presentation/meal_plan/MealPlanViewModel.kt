import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.xml.geminiapp.data.model.MealPlanRequest
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.domain.usecase.GetSelectedLanguageUseCase
import uz.xml.geminiapp.presentation.language.AppLanguage
import uz.xml.geminiapp.presentation.meal_plan.ActivityLevel
import uz.xml.geminiapp.presentation.meal_plan.DietType
import uz.xml.geminiapp.presentation.meal_plan.Goal
import uz.xml.geminiapp.presentation.meal_plan.MealPlanUiState

class MealPlanViewModel(
    private val geminiRepository: GeminiRepository,
    getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
//    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealPlanUiState>(MealPlanUiState.Input)
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    private val currentLanguage = getSelectedLanguageUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppLanguage.ENGLISH
        ).value

    var mealsPerDay by mutableIntStateOf(0)
        private set

    var allergies by mutableStateOf("")
        private set

    var likesDislikes by mutableStateOf("")
        private set

    var dietType by mutableStateOf(DietType.NONE.value)
        private set

    var activityLevel by mutableStateOf(ActivityLevel.MEDIUM.value)
        private set

    var goal by mutableStateOf(Goal.MAINTAIN.value)
        private set

    var cuisineType by mutableStateOf("")
        private set

    var calorieInput by mutableStateOf("")
        private set

    var hasAllergiesError by mutableStateOf(false)
        private set

    var hasLikesDislikesError by mutableStateOf(false)
        private set

    var hasCuisineTypeError by mutableStateOf(false)
        private set

    var hasCalorieError by mutableStateOf(false)
        private set

    var useStoredCalories by mutableStateOf(true)
        private set

    fun getDailyCalories(): Int {
        return 1800
//        return sharedPreferencesManager.getDailyCalories()
    }
    fun hasDailyCalories(): Boolean {
        return false
//        return sharedPreferencesManager.hasDailyCalories()
    }

    fun updateMealsPerDay(value: Int) {
        mealsPerDay = value
    }

    fun updateAllergies(value: String) {
        allergies = value
        hasAllergiesError = false
    }

    fun updateLikesDislikes(value: String) {
        likesDislikes = value
        hasLikesDislikesError = false
    }

    fun updateDietType(value: String) {
        dietType = value
    }

    fun updateActivityLevel(value: String) {
        activityLevel = value
    }

    fun updateGoal(value: String) {
        goal = value
    }

    fun updateCuisineType(value: String) {
        cuisineType = value
        hasCuisineTypeError = false
    }

    fun updateCalorieInput(value: String) {
        calorieInput = value
        if (value.isNotBlank()) {
            hasCalorieError = false
        }
    }

    fun toggleCalorieSource(useStored: Boolean) {
        useStoredCalories = useStored
    }

    fun validateInputs(): Boolean {
        var isValid = true

        if (allergies.isBlank()) {
            hasAllergiesError = true
            isValid = false
        }

        if (likesDislikes.isBlank()) {
            hasLikesDislikesError = true
            isValid = false
        }

        if (cuisineType.isBlank()) {
            hasCuisineTypeError = true
            isValid = false
        }

//        if (!useStoredCalories) {
        if (calorieInput.isBlank() || calorieInput.toIntOrNull() == null || calorieInput.toInt() <= 0) {
            hasCalorieError = true
            isValid = false
        }
//        } else if (!hasDailyCalories()) {
//             If using stored calories but none exist
//            hasCalorieError = true
//            isValid = false
//        }

        return isValid
    }

    fun generateMealPlan() {
        if (!validateInputs()) return

        val dailyCalories =
//            if (useStoredCalories) {
//            getDailyCalories()
//        } else {
            calorieInput.toIntOrNull() ?: 0
//        }

        if (dailyCalories <= 0) {
            hasCalorieError = true
            return
        }

        viewModelScope.launch {
            _uiState.value = MealPlanUiState.Loading
            try {
                val request = MealPlanRequest(
                    dailyCalories = dailyCalories,
                    mealsPerDay = mealsPerDay,
                    allergies = allergies,
                    likesDislikes = likesDislikes,
                    dietType = dietType,
                    activityLevel = activityLevel,
                    cuisineType = cuisineType,
                    goal = goal,
                )

                val result = geminiRepository.generateMealPlan(request, currentLanguage)
                _uiState.value = MealPlanUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = MealPlanUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetToInput() {
        _uiState.value = MealPlanUiState.Input
    }
}
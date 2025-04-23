package uz.xml.geminiapp.presentation.meal_plan

sealed class MealPlanUiState {
    data object Input : MealPlanUiState()
    data object Loading : MealPlanUiState()
    data class Success(val mealPlan: String) : MealPlanUiState()
    data class Error(val message: String) : MealPlanUiState()
}
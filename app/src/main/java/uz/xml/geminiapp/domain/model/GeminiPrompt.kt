package uz.xml.geminiapp.domain.model

import androidx.annotation.StringRes
import uz.xml.geminiapp.R

sealed class GeminiPrompt(@StringRes val textResId: Int) {
    data object CalorieEstimate : GeminiPrompt(R.string.prompt_calorie_estimate)
    data object NutrientBreakdown : GeminiPrompt(R.string.prompt_nutrient_breakdown)
    data object FoodCategorization : GeminiPrompt(R.string.prompt_food_categorization)
    data object FoodSuggestion : GeminiPrompt(R.string.food_suggestion_title)
}

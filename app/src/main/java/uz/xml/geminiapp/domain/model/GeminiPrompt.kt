package uz.xml.geminiapp.domain.model

import androidx.annotation.StringRes
import uz.xml.geminiapp.R

sealed class GeminiPrompt(@StringRes val textResId: Int) {
    data object CalorieEstimate : GeminiPrompt(R.string.prompt_calorie_estimate_en)
    data object NutrientBreakdown : GeminiPrompt(R.string.prompt_nutrient_breakdown_en)
    data object FoodCategorization : GeminiPrompt(R.string.prompt_food_categorization_en)
    data object FoodSuggestion : GeminiPrompt(R.string.food_suggestion_title_en)
}

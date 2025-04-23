package uz.xml.geminiapp.data.model

data class MealPlanRequest(
    val dailyCalories: Int,
    val mealsPerDay: Int,
    val allergies: String,
    val likesDislikes: String,
    val dietType: String,
    val activityLevel: String,
    val cuisineType: String,
    val goal: String,
)
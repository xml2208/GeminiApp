package uz.xml.geminiapp.presentation.daily_calorie

data class CalorieFormState(
    val gender: String,
    val activity: String,
    val goal: String,
    val age: String = "",
    val height: String = "",
    val weight: String = "",
)

data class CalorieScreenOptions(
    val genderOptions: List<String>,
    val activityOptions: List<String>,
    val goalOptions: List<String>
)
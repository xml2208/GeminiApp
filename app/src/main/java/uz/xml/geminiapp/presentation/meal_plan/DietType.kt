package uz.xml.geminiapp.presentation.meal_plan

enum class DietType(val value: String) {
    NONE("None"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    KETO("Keto"),
    HEALTHY("Healthy Eating")
}

enum class ActivityLevel(val value: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class Goal(val value: String) {
    MAINTAIN("Maintaining weight"),
    LOSE_WEIGHT("Lose weight"),
    GAIN_WEIGHT("Gain weight")
}
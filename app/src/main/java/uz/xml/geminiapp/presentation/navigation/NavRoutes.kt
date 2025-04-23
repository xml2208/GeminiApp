package uz.xml.geminiapp.presentation.navigation

import uz.xml.geminiapp.presentation.language.AppLanguage

/**
 * Navigation route constants for the Calorie app
 */
object NavRoutes {
    const val WELCOME = "welcome"
    const val CAMERA = "camera"
    const val SETTINGS = "settings"
    const val USER_DAILY_CALORIE = "user_daily_calorie"
    const val MEAL_PLAN = "meal_plan"
    
    object ResultScreen {
        private const val ROUTE = "result_screen"
        const val IMAGE_URI = "imageUri"
        const val PROMPT_TYPE = "promptType"
        const val LANGUAGE = "language"
        
        val route = "$ROUTE/{$IMAGE_URI}/{$PROMPT_TYPE}/{$LANGUAGE}"

        fun createRoute(imageUri: String, promptType: String, language: AppLanguage): String {
            return "$ROUTE/$imageUri/$promptType/$language"
        }
    }
}
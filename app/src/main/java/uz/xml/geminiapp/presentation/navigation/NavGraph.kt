package uz.xml.geminiapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.MealScanScreen
import uz.xml.geminiapp.presentation.analysis.ResultScreen
import uz.xml.geminiapp.presentation.camera.AppLanguage
import uz.xml.geminiapp.presentation.camera.CameraScreen

@Composable
fun CalorieApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "welcome") {
        composable("welcome") {
            MealScanScreen(navController)
        }
        composable("camera") {
            CameraScreen(navController = navController)
        }
        composable(
            route = "result_screen/{imageUri}/{promptType}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("promptType") { type = NavType.StringType },
//                navArgument("language") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoUriString =
                backStackEntry.arguments?.getString("imageUri") ?: return@composable
            val promptTypeString =
                backStackEntry.arguments?.getString("promptType") ?: return@composable
//            val languageString =
//                backStackEntry.arguments?.getString("language") ?: return@composable

            val promptType = when (promptTypeString) {
                "CalorieEstimate" -> GeminiPrompt.CalorieEstimate
                "NutrientBreakdown" -> GeminiPrompt.NutrientBreakdown
                "FoodCategorization" -> GeminiPrompt.FoodCategorization
                "FoodSuggestion" -> GeminiPrompt.FoodSuggestion
                else -> GeminiPrompt.CalorieEstimate
            }

//            val language = AppLanguage.valueOf(languageString)

            ResultScreen(
                imageUri = photoUriString.toUri(),
                prompt = promptType,
                language = AppLanguage.ENGLISH,
            )
        }
    }
}
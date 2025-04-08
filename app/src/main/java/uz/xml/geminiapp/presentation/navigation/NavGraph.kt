package uz.xml.geminiapp.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.xml.geminiapp.presentation.camera.CameraScreen
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.navArgument
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.MealScanScreen
import uz.xml.geminiapp.presentation.analyze.AnalysisScreen
import uz.xml.geminiapp.presentation.analyze.ResultScreen
import uz.xml.geminiapp.presentation.camera.AppLanguage

@Composable
fun CalorieApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "welcome") {
        composable("welcome") {
            MealScanScreen(navController)
        }

        composable("camera") {
            CameraScreen(
                navController = navController,
                onBack = { navController.navigateUp() }
            )
        }
        composable(
            route = "analyze/{imageUri}",
            arguments = listOf(navArgument("imageUri", builder = { NavType.StringType }))
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("imageUri") ?: return@composable
            val decodedUri = Uri.decode(uriString)
            AnalysisScreen(
                photoUri = decodedUri.toUri(),
                navController = navController,
            )
        }
        composable(
            route = "result_screen/{imageUri}/{promptType}/{language}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("promptType") { type = NavType.StringType },
                navArgument("language") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoUriString =
                backStackEntry.arguments?.getString("imageUri") ?: return@composable
            val promptTypeString =
                backStackEntry.arguments?.getString("promptType") ?: return@composable
            val languageString =
                backStackEntry.arguments?.getString("language") ?: return@composable

            val promptType = when (promptTypeString) {
                "CalorieEstimate" -> GeminiPrompt.CalorieEstimate
                "NutrientBreakdown" -> GeminiPrompt.NutrientBreakdown
                "FoodCategorization" -> GeminiPrompt.FoodCategorization
                "FoodSuggestion" -> GeminiPrompt.FoodSuggestion
                else -> GeminiPrompt.CalorieEstimate
            }

            val language = AppLanguage.valueOf(languageString)

            ResultScreen(
                imageUri = photoUriString.toUri(),
                prompt = promptType,
                language = language,
            )
        }
    }
}
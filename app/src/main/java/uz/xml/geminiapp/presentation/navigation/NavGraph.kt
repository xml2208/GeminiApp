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
import uz.xml.geminiapp.presentation.camera.CameraScreen
import uz.xml.geminiapp.presentation.daily_calorie.DailyCaloriesEstimationScreen
import uz.xml.geminiapp.presentation.language.AppLanguage
import uz.xml.geminiapp.presentation.profile.SettingsScreen

@Composable
fun CalorieApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NavRoutes.WELCOME) {
        composable(NavRoutes.WELCOME) {
            MealScanScreen(navController)
        }

        composable(NavRoutes.CAMERA) {
            CameraScreen(navController = navController)
        }

        composable(
            route = NavRoutes.ResultScreen.route,
            arguments = listOf(
                navArgument(NavRoutes.ResultScreen.IMAGE_URI) { type = NavType.StringType },
                navArgument(NavRoutes.ResultScreen.PROMPT_TYPE) { type = NavType.StringType },
                navArgument(NavRoutes.ResultScreen.LANGUAGE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoUriString =
                backStackEntry.arguments?.getString(NavRoutes.ResultScreen.IMAGE_URI) ?: return@composable
            val promptTypeString =
                backStackEntry.arguments?.getString(NavRoutes.ResultScreen.PROMPT_TYPE).orEmpty()
            val languageString =
                backStackEntry.arguments?.getString(NavRoutes.ResultScreen.LANGUAGE) ?: return@composable
            val promptType = GeminiPrompt.fromString(promptTypeString)

            ResultScreen(
                imageUri = photoUriString.toUri(),
                prompt = promptType,
                language = AppLanguage.valueOf(languageString),
            )
        }

        composable(route = NavRoutes.SETTINGS) {
            SettingsScreen(navController)
        }

        composable(route = NavRoutes.USER_DAILY_CALORIE) {
            DailyCaloriesEstimationScreen()
        }
    }
}
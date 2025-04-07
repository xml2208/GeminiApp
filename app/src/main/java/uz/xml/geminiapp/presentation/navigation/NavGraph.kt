package uz.xml.geminiapp.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.xml.geminiapp.presentation.analyze.AnalyzeScreen
import uz.xml.geminiapp.presentation.camera.CameraScreen
import androidx.core.net.toUri
import uz.xml.geminiapp.presentation.MealScanScreen

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
        composable("analyze/{imageUri}") { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("imageUri")
            val decodedUri = Uri.decode(uriString)
            AnalyzeScreen(decodedUri.toUri())
        }
    }
}
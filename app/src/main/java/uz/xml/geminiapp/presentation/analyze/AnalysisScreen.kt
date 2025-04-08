package uz.xml.geminiapp.presentation.analyze

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.camera.AppLanguage
import uz.xml.geminiapp.presentation.camera.LanguageViewModel
import uz.xml.geminiapp.presentation.camera.LanguageToggle

@Composable
fun AnalysisScreen(
    languageViewModel: LanguageViewModel = koinViewModel(),
    photoUri: Uri,
    navController: NavController
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LanguageToggle(
            currentLanguage = currentLanguage,
            onToggle = { languageViewModel.toggleLanguage() },
            modifier = Modifier
        )

        AsyncImage(
            model = photoUri,
            contentDescription = "Captured food image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.4f),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(
                id = if (currentLanguage == AppLanguage.ENGLISH)
                    R.string.what_to_analyze_en
                else
                    R.string.what_to_analyze_uz
            ),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnalysisOptionButton(
            promptType = GeminiPrompt.CalorieEstimate,
            uri = photoUri,
            navController = navController,
            currentLanguage = currentLanguage
        )

        AnalysisOptionButton(
            promptType = GeminiPrompt.NutrientBreakdown,
            uri = photoUri,
            navController = navController,
            currentLanguage = currentLanguage
        )

        AnalysisOptionButton(
            promptType = GeminiPrompt.FoodCategorization,
            uri = photoUri,
            navController = navController,
            currentLanguage = currentLanguage
        )

        AnalysisOptionButton(
            promptType = GeminiPrompt.FoodSuggestion,
            uri = photoUri,
            navController = navController,
            currentLanguage = currentLanguage
        )

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            content = {
                Icon(Icons.Default.ArrowBack, null)
            }
        )
    }
}

@Composable
private fun AnalysisOptionButton(
    promptType: GeminiPrompt,
    uri: Uri,
    navController: NavController,
    currentLanguage: AppLanguage
) {
    val buttonTextResId = when (promptType) {
        is GeminiPrompt.CalorieEstimate -> if (currentLanguage == AppLanguage.ENGLISH)
            R.string.prompt_calorie_estimate_button_en else R.string.prompt_calorie_estimate_button_uz

        is GeminiPrompt.NutrientBreakdown -> if (currentLanguage == AppLanguage.ENGLISH)
            R.string.prompt_nutrient_breakdown_button_en else R.string.prompt_nutrient_breakdown_button_uz

        is GeminiPrompt.FoodCategorization -> if (currentLanguage == AppLanguage.ENGLISH)
            R.string.prompt_food_categorization_button_en else R.string.prompt_food_categorization_button_uz

        is GeminiPrompt.FoodSuggestion -> if (currentLanguage == AppLanguage.ENGLISH)
            R.string.prompt_food_suggestion_button_en else R.string.prompt_food_suggestion_button_uz
    }

    Button(
        onClick = {
            val encodedUri = Uri.encode(uri.toString())
            navController.navigate(
                route = "result_screen/${encodedUri}/${promptType.javaClass.simpleName}/${currentLanguage.name}"
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text(text = stringResource(id = buttonTextResId))
    }
}
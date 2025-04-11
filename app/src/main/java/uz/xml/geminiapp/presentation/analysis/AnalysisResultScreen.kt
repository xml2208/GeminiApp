package uz.xml.geminiapp.presentation.analysis

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.camera.AppLanguage
import uz.xml.geminiapp.presentation.composable.TypingTextAnimation

@Composable
fun ResultScreen(
    imageUri: Uri,
    prompt: GeminiPrompt,
    language: AppLanguage,
    viewModel: AnalyzeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val viewState = viewModel.analysisState

    LaunchedEffect(imageUri) {
        viewModel.analyzeImage(
            context = context,
            imageUri = imageUri,
            promptType = prompt,
            appLanguage = language
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Captured Food image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.4f)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        val titleResId = when (prompt) {
            is GeminiPrompt.CalorieEstimate -> if (language == AppLanguage.ENGLISH)
                R.string.calorie_estimate_title_en else R.string.calorie_estimate_title_uz

            is GeminiPrompt.NutrientBreakdown -> if (language == AppLanguage.ENGLISH)
                R.string.nutrient_breakdown_title_en else R.string.nutrient_breakdown_title_uz

            is GeminiPrompt.FoodCategorization -> if (language == AppLanguage.ENGLISH)
                R.string.food_categorization_title_en else R.string.food_categorization_title_uz

            is GeminiPrompt.FoodSuggestion -> if (language == AppLanguage.ENGLISH)
                R.string.food_suggestion_title_en else R.string.food_suggestion_title_uz
        }

        Text(
            text = stringResource(id = titleResId),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (viewState) {
            is AnalysisState.Loading -> {
                TypingTextAnimation(fullText = viewState.loadingText)
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 32.dp),
                    color = Color.Black
                )
            }

            is AnalysisState.Success -> {
                TypingTextAnimation(fullText = viewState.result)
            }

            is AnalysisState.Error -> {
                Text(
                    "Error: ${viewState.message}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
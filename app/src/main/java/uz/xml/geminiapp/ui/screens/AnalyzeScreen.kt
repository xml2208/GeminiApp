package uz.xml.geminiapp.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.xml.geminiapp.BuildConfig
import uz.xml.geminiapp.R

@Composable
fun AnalyzeScreen(imageUri: Uri) {
    val context = LocalContext.current
    var uiState by remember { mutableStateOf<AnalysisState>(AnalysisState.Loading) }
    var isUzbekLanguage by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = imageUri, key2 = isUzbekLanguage) {
        try {
            val bitmap = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(imageUri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            }

            if (bitmap == null) {
                uiState = AnalysisState.Error("Failed to load image")
                return@LaunchedEffect
            }

            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.API_KEY
            )

            val promptText = if (isUzbekLanguage) {
                "Rasmdagi ovqatlarni aniqlang va umumiy kaloriyani taxminlang. O'zbek tilida javob bering."
            } else {
                "Identify the foods in the image and estimate total calories."
            }
            val inputContent = content {
                image(bitmap)
                text(promptText)
            }

            val response = model.generateContent(inputContent)
            uiState = AnalysisState.Success(response.text.orEmpty())

        } catch (e: Exception) {
            uiState = AnalysisState.Error(e.message ?: "Unknown error occurred")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp)
        ) {
            UzbekLanguageToggle(
                isUzbekSelected = isUzbekLanguage,
                onToggle = {
                    uiState = AnalysisState.Loading
                    isUzbekLanguage = it
                }
            )
        }

        when (val state = uiState) {
            is AnalysisState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
            }

            is AnalysisState.Success -> {
                Text(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    text = state.result,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
            }

            is AnalysisState.Error -> {
                Text(
                    "Error: ${state.message}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun UzbekLanguageToggle(
    isUzbekSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onToggle(!isUzbekSelected) }
    ) {
        Image(
            painter = painterResource(id = R.drawable.uzb),
            contentDescription = "Uzbek language toggle",
            modifier = Modifier
                .size(32.dp)
                .padding(end = 4.dp)
        )

        Text(
            text = if (isUzbekSelected) "UZ" else "EN",
            style = MaterialTheme.typography.labelMedium,
            color = if (isUzbekSelected) Color(0xFF0072CE) else Color.Gray
        )
    }
}

private sealed class AnalysisState {
    data object Loading : AnalysisState()
    data class Success(val result: String) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
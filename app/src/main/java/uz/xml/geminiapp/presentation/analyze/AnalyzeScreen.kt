package uz.xml.geminiapp.presentation.analyze

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.composable.TypingTextAnimation

@Composable
fun AnalyzeScreen(imageUri: Uri) {
    val context = LocalContext.current
    val viewModel: AnalyzeViewModel = koinViewModel()
    val viewState = viewModel.analysisState

    LaunchedEffect(imageUri) {
        viewModel.analyzeImage(context, imageUri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp)
        ) {
//            UzbekLanguageToggle(
//                modifier = Modifier.align(Alignment.TopEnd),
//                isUzbekSelected = isUzbekLanguage,
//                onToggle = {
//                    viewState = AnalysisState.Loading()
//                    isUzbekLanguage = it
//                }
//            )
        }

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

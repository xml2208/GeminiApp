package uz.xml.geminiapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uz.xml.geminiapp.presentation.camera.CameraScreen

@Composable
fun MealScanScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showCamera by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (showCamera) {
            CameraScreen(
                navController = navController,
                onBack = { showCamera = false }
            )
        } else {
            WelcomeScreen(onScanClick = { showCamera = true })
        }
    }
}

@Composable
private fun WelcomeScreen(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
            model = "https://www.watersedgechc.com/wp-content/uploads/2024/04/The-Difference-Between-a-Nutritionist-and-a-Registered-Dietitian.jpg",
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            text = "AI powered\n\n Meal Calorie\n\n Analyzer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.weight(1f))

        Text(
            text = "Take a photo of your meal to analyze its contents and estimate calories",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "Scan Meal",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(16.dp), color = Color.Black)
                .padding(12.dp)
                .clickable { onScanClick() }
        )
    }
}


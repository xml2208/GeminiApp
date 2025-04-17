package uz.xml.geminiapp.presentation.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.language.AppLanguage
import uz.xml.geminiapp.presentation.navigation.NavRoutes
import java.io.File
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedLanguage by viewModel.currentLanguage.collectAsState(AppLanguage.ENGLISH)

    CameraScreenContent(
        capturedPhotoUri = uiState.capturedPhotoUri,
        hasValidPhoto = uiState.hasValidPhoto,
        onPhotoCapture = viewModel::saveCapturedPhoto,
        customPromptUiState = uiState.customPromptUiState,
        onNavigateBack = { navController.navigateUp() },
        onOtherButtonClicked = { viewModel.showCustomPromptDialog() },
        onNavigateToResult = { uri, prompt ->
            val encodedUri = Uri.encode(uri.toString())
            val promptString = GeminiPrompt.toString(prompt)
            navController.navigate(
                NavRoutes.ResultScreen.createRoute(
                    imageUri = encodedUri,
                    promptType = promptString,
                    language = selectedLanguage
                )
            )
        }
    )
}

@Composable
fun CameraScreenContent(
    capturedPhotoUri: Uri?,
    hasValidPhoto: Boolean,
    customPromptUiState: CustomPromptUiState,
    onOtherButtonClicked: () -> Unit,
    onPhotoCapture: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Uri, GeminiPrompt) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ) {
            if (capturedPhotoUri == null) {
                CameraPreview(
                    imageCapture = imageCapture,
                    executor = executor,
                    onImageCaptured = onPhotoCapture,
                    onBack = onNavigateBack,
                )
            } else {
                AsyncImage(
                    model = capturedPhotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        GeminiPromptsContent(
            hasValidPhoto = hasValidPhoto,
            onPromptSelected = { prompt ->
                capturedPhotoUri?.let { uri -> onNavigateToResult(uri, prompt) }
            },
            modifier = Modifier
                .weight(1f)
                .padding(top = 26.dp),
            onOtherButtonClicked = { onOtherButtonClicked() },
            customPromptUiState = customPromptUiState
        )
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    executor: Executor,
    onBack: () -> Unit,
    onImageCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(lifecycleOwner) {
        var cameraProvider: ProcessCameraProvider? = null

        val listenableFuture = ProcessCameraProvider.getInstance(context)
        listenableFuture.addListener({
            try {
                cameraProvider = listenableFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Camera binding failed", e)
            }
        }, executor)

        onDispose { cameraProvider?.unbindAll() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        CaptureButton(
            imageCapture = imageCapture,
            executor = executor,
            onImageCaptured = onImageCaptured,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun CaptureButton(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    IconButton(
        modifier = modifier,
        onClick = {
            val outputFile = createImageFile(context)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val uri = Uri.fromFile(outputFile)
                        onImageCaptured(uri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraScreen", "Image capture failed", exception)
                    }
                }
            )
        },
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_capture),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = Color.White
        )
    }
}


private fun createImageFile(context: Context): File {
    return File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
}

@Composable
private fun GeminiPromptsContent(
    hasValidPhoto: Boolean,
    onPromptSelected: (GeminiPrompt) -> Unit,
    onOtherButtonClicked: () -> Unit,
    customPromptUiState: CustomPromptUiState,
    modifier: Modifier = Modifier,
) {
    val prompts = remember {
        listOf(
            GeminiPrompt.CalorieEstimate,
            GeminiPrompt.NutrientBreakdown,
            GeminiPrompt.FoodSuggestion,
            GeminiPrompt.FoodCategorization
        )
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.what_to_analyze),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(prompts) { prompt ->
                GeminiPromptButton(
                    prompt = prompt,
                    enabled = hasValidPhoto,
                    onClick = { onPromptSelected(prompt) }
                )
            }
            item {
                GeminiPromptButton(
                    prompt = GeminiPrompt.Other(customPrompt = customPromptUiState.customPrompt),
                    enabled = hasValidPhoto,
                    onClick = { onOtherButtonClicked() }
                )
            }
        }
        if (customPromptUiState.showDialog) {
            UserCustomPromptDialog(
                customPromptUiState = customPromptUiState,
                onPromptSelected = {
                    onPromptSelected(GeminiPrompt.Other(customPromptUiState.customPrompt))
                })
        }
    }
}

@Composable
private fun GeminiPromptButton(
    prompt: GeminiPrompt,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val buttonText = when (prompt) {
        is GeminiPrompt.CalorieEstimate -> stringResource(R.string.prompt_calorie_estimate_button)
        is GeminiPrompt.NutrientBreakdown -> stringResource(R.string.prompt_nutrient_breakdown_button)
        is GeminiPrompt.FoodCategorization -> stringResource(R.string.prompt_food_categorization_button)
        is GeminiPrompt.FoodSuggestion -> stringResource(R.string.prompt_food_suggestion_button)
        is GeminiPrompt.Other -> prompt.customPrompt.ifBlank { stringResource(R.string.custom_prompt) }
    }

    val backgroundColor = if (enabled) Color.Black else Color.Gray

    Text(
        text = buttonText,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(18.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
    )
}

@Composable
fun UserCustomPromptDialog(
    customPromptUiState: CustomPromptUiState,
    onPromptSelected: (GeminiPrompt) -> Unit,
) {
    if (customPromptUiState.showDialog) {
        AlertDialog(
            onDismissRequest = { customPromptUiState.onDismiss() },
            text = {
                TextField(
                    value = customPromptUiState.customPrompt,
                    onValueChange = { customPromptUiState.onValueChange(it) },
                    label = { Text(stringResource(R.string.enter_your_prompt)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPromptSelected(GeminiPrompt.Other(customPromptUiState.customPrompt))
                        customPromptUiState.onConfirm()
                    },
                    content = { Text(stringResource(R.string.ok)) },
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { customPromptUiState.onDismiss },
                    content = { Text(stringResource(R.string.cancel)) }
                )
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun AnalyzeOptionItemPreview() {
    GeminiPromptButton(
        prompt = GeminiPrompt.FoodSuggestion,
        onClick = {},
        enabled = false,
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CameraScreenPreview(modifier: Modifier = Modifier) {
    CameraScreenContent(
        capturedPhotoUri = null,
        hasValidPhoto = false,
        customPromptUiState = CustomPromptUiState(),
        onOtherButtonClicked = { },
        onPhotoCapture = { },
        onNavigateBack = { },
        onNavigateToResult = { _, _ -> },
        modifier = Modifier
    )
}
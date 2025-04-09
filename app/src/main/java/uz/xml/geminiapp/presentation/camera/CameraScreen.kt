package uz.xml.geminiapp.presentation.camera

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import uz.xml.geminiapp.R
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            imageCapture = imageCapture,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.4f),
        )

        Icon(
            imageVector = Icons.Default.ArrowBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable { onBack() },
            contentDescription = null
        )

        CaptureButton(
            imageCapture = imageCapture,
            executor = executor,
            onImageCaptured = { uri ->
                val encodedUri = Uri.encode(uri.toString())
                navController.navigate("analyze/$encodedUri")
            },
            onError = { Log.e("CameraScreen", "Image capture failed", it) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    LaunchedEffect(previewView) {
        val cameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
            ProcessCameraProvider.getInstance(context).apply {
                addListener({
                    try {
                        continuation.resume(get())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }, executor)
            }
        }

        try {
            cameraProvider.unbindAll()

            val preview =
                Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Camera binding failed", e)
        }
    }
}

@Composable
private fun CaptureButton(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val uri = Uri.fromFile(file)
                        onImageCaptured(uri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        onError(exception)
                    }
                }
            )
        },

        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text("Capture")
    }
}


@Composable
fun LanguageToggle(
    currentLanguage: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onToggle() }
    ) {
        Image(
            painter = painterResource(
                id = if (currentLanguage == AppLanguage.ENGLISH)
                    R.drawable.ic_uzbek_flag
                else
                    R.drawable.ic_american_flag
            ),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 4.dp)
        )
    }
}

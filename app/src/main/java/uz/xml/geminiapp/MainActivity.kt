package uz.xml.geminiapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

const val API_KEY = "AIzaSyA5tGSY2D7rBZySuVVpktou5j01_I4FoVM"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val scrollState = rememberScrollState()
            val coroutine = rememberCoroutineScope()
            val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = API_KEY)
            var queryText by remember { mutableStateOf("") }
            var generatedText by remember { mutableStateOf("") }
            var isLoading by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PromptWithImagesContent(
                    analyze = {
                        isLoading = true
                        coroutine.launch {
                            try {
                                val rawImgUrl = "https://foodboxhq.com/wp-content/uploads/2023/02/friends-cooking-together-1.webp"
                                val b = generativeModel.generateContent(prompt = "Describe this photo in detail $it")
                                val generatedContentResponse = generativeModel.generateContent(prompt = "What is the subject of the photo  $rawImgUrl")
                                Log.d("TAG", "onCreate: ${generatedContentResponse.text}")
                                generatedText = generatedContentResponse.text.orEmpty()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
//                PromptWithTextContent(
//                    query = queryText,
//                    onTextChange = { queryText = it },
//                    onSend = {
//                        isLoading = true
//                        coroutine.launch {
//                            try {
//                                val generateContent =
//                                    generativeModel.generateContent(prompt = queryText)
//                                generatedText = generateContent.text.orEmpty()
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            } finally {
//                                isLoading = false
//                            }
//                        }
//                    }
//                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier)
                } else {
                    Text(
                        modifier = Modifier.verticalScroll(scrollState),
                        text = generatedText
                    )
                }
            }
        }
    }

    @Composable
    fun PromptWithTextContent(
        query: String,
        onTextChange: (String) -> Unit,
        onSend: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = query,
                placeholder = { Text(text = "Enter your prompt here...") },
                onValueChange = onTextChange
            )
            Text(
                text = "Send",
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 10.dp)
                    .clickable { onSend() }
            )
        }

    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun PromptWithImagesContent(
        analyze:(Uri) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current
        val cameraPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        )
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { imageUri = it }
        )

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Select Photo",
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 10.dp)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    }
            )
            AsyncImage(
                modifier = Modifier.size(160.dp),
                model = imageUri,
                contentDescription = null
            )

            Text(
                text = "Analyze Photo",
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 10.dp)
                    .clickable { imageUri?.let { analyze(it) } }
            )

        }

    }
}
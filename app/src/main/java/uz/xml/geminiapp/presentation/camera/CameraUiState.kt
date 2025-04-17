package uz.xml.geminiapp.presentation.camera

import android.net.Uri

data class CameraUiState(
    val capturedPhotoUri: Uri? = null,
    val hasValidPhoto: Boolean = false,
    val customPromptUiState: CustomPromptUiState = CustomPromptUiState()

)

data class CustomPromptUiState(
    val customPrompt: String = "",
    val onDismiss: () -> Unit = {},
    val onValueChange: (String) -> Unit = {},
    val onConfirm: () -> Unit = {},
    val showDialog: Boolean = false,
)
package uz.xml.geminiapp.presentation.camera

import android.net.Uri

data class CameraUiState(
    val capturedPhotoUri: Uri? = null,
    val hasValidPhoto: Boolean = false
)
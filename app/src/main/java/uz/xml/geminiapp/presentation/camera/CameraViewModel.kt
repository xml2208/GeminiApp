package uz.xml.geminiapp.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel : ViewModel() {

    private val _uiSate = MutableStateFlow(CameraUiState())
    val uiState = _uiSate.asStateFlow()

    fun saveCapturedPhoto(uri: Uri?) {
        _uiSate.update { currentState ->
            currentState.copy(
                capturedPhotoUri = uri,
                hasValidPhoto = uri != null
            )
        }
    }

}
package uz.xml.geminiapp.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import uz.xml.geminiapp.domain.usecase.GetSelectedLanguageUseCase
import uz.xml.geminiapp.presentation.language.AppLanguage

class CameraViewModel(
    getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
) : ViewModel() {

    private val _uiSate = MutableStateFlow(CameraUiState())
    val uiState = _uiSate.asStateFlow()

    val currentLanguage = getSelectedLanguageUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppLanguage.ENGLISH
        )

    fun saveCapturedPhoto(uri: Uri?) {
        _uiSate.update { currentState ->
            currentState.copy(
                capturedPhotoUri = uri,
                hasValidPhoto = uri != null
            )
        }
    }

    fun onCustomPromptChanged(prompt: String) {
        _uiSate.update { currentState ->
            currentState.copy(
                customPromptUiState = currentState.customPromptUiState.copy(
                    customPrompt = prompt
                )
            )
        }
    }

    fun onCustomPromptConfirmed() {
        _uiSate.update { currentState ->
            currentState.copy(
                customPromptUiState = currentState.customPromptUiState.copy(
                    customPrompt = "",
                    showDialog = false
                )
            )
        }
    }

    fun onCustomPromptDismissed() {
        _uiSate.update { currentState ->
            currentState.copy(
                customPromptUiState = currentState.customPromptUiState.copy(
                    customPrompt = "",
                    showDialog = false
                )
            )
        }
    }

    fun showCustomPromptDialog() {
        _uiSate.update { currentState ->
            currentState.copy(
                customPromptUiState = currentState.customPromptUiState.copy(
                    showDialog = true,
                    onDismiss = ::onCustomPromptDismissed,
                    onValueChange = ::onCustomPromptChanged,
                    onConfirm = ::onCustomPromptConfirmed
                )
            )
        }
    }
}
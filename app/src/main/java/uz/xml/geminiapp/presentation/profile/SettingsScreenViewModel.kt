package uz.xml.geminiapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.xml.geminiapp.domain.repository.SettingsRepository
import uz.xml.geminiapp.presentation.language.AppLanguage

class SettingsScreenViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val selectedLanguage: Flow<AppLanguage> = settingsRepository.selectedLanguage
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.ENGLISH)

    fun changeLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
}
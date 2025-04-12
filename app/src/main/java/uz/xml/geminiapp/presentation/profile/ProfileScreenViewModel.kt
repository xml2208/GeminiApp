package uz.xml.geminiapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.xml.geminiapp.domain.repository.ProfileRepository
import uz.xml.geminiapp.presentation.language.AppLanguage

class ProfileScreenViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    val selectedLanguage: Flow<AppLanguage> = profileRepository.selectedLanguage
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.ENGLISH)

    fun changeLanguage(language: AppLanguage) {
        viewModelScope.launch {
            profileRepository.setLanguage(language)
        }
    }
}
package uz.xml.geminiapp.domain.usecase

import uz.xml.geminiapp.domain.repository.SettingsRepository
import uz.xml.geminiapp.presentation.language.AppLanguage

class UpdateSelectedLanguageUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(language: AppLanguage) {
        settingsRepository.setLanguage(language)
    }
}
package uz.xml.geminiapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.xml.geminiapp.domain.repository.SettingsRepository
import uz.xml.geminiapp.presentation.language.AppLanguage

class GetSelectedLanguageUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<AppLanguage> = settingsRepository.selectedLanguage
}
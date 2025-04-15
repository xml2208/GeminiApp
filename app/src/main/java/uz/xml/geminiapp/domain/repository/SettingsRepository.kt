package uz.xml.geminiapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.xml.geminiapp.presentation.language.AppLanguage

interface SettingsRepository {
    val selectedLanguage: Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}
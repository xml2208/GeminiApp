package uz.xml.geminiapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.xml.geminiapp.presentation.language.AppLanguage

interface ProfileRepository {
    val selectedLanguage: Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}
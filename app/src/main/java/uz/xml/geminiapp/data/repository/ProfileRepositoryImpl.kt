package uz.xml.geminiapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.xml.geminiapp.domain.repository.ProfileRepository
import uz.xml.geminiapp.presentation.language.AppLanguage

class ProfileRepositoryImpl(context: Context): ProfileRepository {
    private val Context.dataStore by preferencesDataStore("settings")

    private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    private val dataStore = context.dataStore

    override val selectedLanguage: Flow<AppLanguage> = dataStore.data
        .map { preferences ->
            AppLanguage.fromCode(preferences[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.code)
        }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[LANGUAGE_KEY] = language.code }
    }
}
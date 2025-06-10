package uz.xml.geminiapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uz.xml.geminiapp.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val caloriesFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_CALORIES].orEmpty()
        }

    override suspend fun saveCalories(calories: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_CALORIES] = calories
        }
    }

    override suspend fun getCalories(): String {
        return dataStore.data.first()[PreferencesKeys.DAILY_CALORIES].orEmpty()
    }
}

object PreferencesKeys {
    val DAILY_CALORIES = stringPreferencesKey("daily_calories")
}
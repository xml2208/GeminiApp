package uz.xml.geminiapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val caloriesFlow: Flow<String>

    suspend fun saveCalories(calories: String)

    suspend fun getCalories(): String
}
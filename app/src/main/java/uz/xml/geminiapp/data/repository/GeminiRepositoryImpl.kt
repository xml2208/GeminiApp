package uz.xml.geminiapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.xml.geminiapp.R
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.presentation.language.AppLanguage
import uz.xml.geminiapp.presentation.language.LanguageManager.getLocalizedContext

class GeminiRepositoryImpl(
    private val generativeModel: GenerativeModel,
    private val context: Context,
) : GeminiRepository {

    override suspend fun analyzeFood(
        bitmap: Bitmap,
        promptType: GeminiPrompt,
        language: AppLanguage,
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val localizedContext = context.getLocalizedContext(language)
                val promptText = localizedContext.getString(promptType.textResId)
                val inputContent = content {
                    image(bitmap)
                    text(promptText)
                }
                val response = generativeModel.generateContent(inputContent)
                response.text ?: context.getString(R.string.analysis_error_text)

            } catch (e: Exception) {
                throw Exception("Failed to analyze image: ${e.message}")
            }
        }
    }

    override suspend fun estimateDailyCalories(
        gender: String,
        age: Int,
        heightCm: Int,
        weightKg: Int,
        activityLevel: String,
        goal: String,
        language: AppLanguage,
    ): String {
        val localizedContext = context.getLocalizedContext(language)

        val prompt = """${localizedContext.getString(R.string.calorie_prompt_intro)}
                - ${localizedContext.getString(R.string.gender_label)}: $gender
                - ${localizedContext.getString(R.string.age_label)}: $age
                - ${localizedContext.getString(R.string.height_label)}: $heightCm cm
                - ${localizedContext.getString(R.string.weight_label)}: $weightKg kg
                - ${localizedContext.getString(R.string.activity_label)}: $activityLevel
                - ${localizedContext.getString(R.string.goal_label)}: $goal ${
            localizedContext.getString(R.string.calorie_goal_desc)
        }  ${localizedContext.getString(R.string.calorie_prompt_conclusion)}
           """.trimIndent()

        return withContext(Dispatchers.IO) {
            try {
                val input = content { text(prompt) }
                val response = generativeModel.generateContent(input)
                response.text ?: "No response"
            } catch (e: Exception) {
                throw Exception(context.getString(R.string.failure_estimation, e.message))
            }
        }
    }
}
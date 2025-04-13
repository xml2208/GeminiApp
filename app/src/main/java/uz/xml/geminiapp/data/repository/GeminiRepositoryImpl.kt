package uz.xml.geminiapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                response.text ?: if (language == AppLanguage.ENGLISH) {
                    "No analysis result available"
                } else {
                    "Tahlil natijasi mavjud emas"
                }
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
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                Calculate the estimated daily calorie intake for a person with the following details:
                - Gender: $gender
                - Age: $age
                - Height: $heightCm cm
                - Weight: $weightKg kg
                - Activity level: $activityLevel
                - Goal: $goal (maintain, lose, or gain weight)
                
                Provide the calorie amount in kcal per day and a short explanation.
            """.trimIndent()

                val input = content { text(prompt) }
                val response = generativeModel.generateContent(input)
                response.text ?: "No response"
            } catch (e: Exception) {
                throw Exception("Failed to estimate calories: ${e.message}")
            }
        }
    }
}
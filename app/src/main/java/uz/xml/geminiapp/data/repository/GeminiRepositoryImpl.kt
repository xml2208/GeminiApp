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
import uz.xml.geminiapp.presentation.camera.AppLanguage

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
                val promptResId = when (promptType) {
                    GeminiPrompt.CalorieEstimate -> if (language == AppLanguage.ENGLISH)
                        R.string.prompt_calorie_estimate_en else R.string.prompt_calorie_estimate_uz

                    GeminiPrompt.NutrientBreakdown -> if (language == AppLanguage.ENGLISH)
                        R.string.prompt_nutrient_breakdown_en else R.string.prompt_nutrient_breakdown_uz

                    GeminiPrompt.FoodCategorization -> if (language == AppLanguage.ENGLISH)
                        R.string.prompt_food_categorization_en else R.string.prompt_food_categorization_uz

                    GeminiPrompt.FoodSuggestion -> if (language == AppLanguage.ENGLISH)
                        R.string.prompt_food_suggestion_en else R.string.prompt_food_suggestion_uz
                }
                val promptText = context.getString(promptResId)
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
}
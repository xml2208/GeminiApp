package uz.xml.geminiapp.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import uz.xml.geminiapp.domain.repository.GeminiRepository

class GeminiRepositoryImpl(
    private val generativeModel: GenerativeModel
) : GeminiRepository {

    override suspend fun analyzeFood(bitmap: Bitmap): String {
        val inputContent = content {
            image(bitmap)
            text("Identify the food in this image and estimate total calories.")
        }

        val response = generativeModel.generateContent(inputContent)
        return response.text ?: "No result"
    }
}
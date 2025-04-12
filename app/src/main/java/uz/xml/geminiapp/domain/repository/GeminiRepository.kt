package uz.xml.geminiapp.domain.repository

import android.graphics.Bitmap
import uz.xml.geminiapp.domain.model.GeminiPrompt
import uz.xml.geminiapp.presentation.language.AppLanguage

interface GeminiRepository {
    suspend fun analyzeFood(
        bitmap: Bitmap,
        promptType: GeminiPrompt,
        language: AppLanguage,
    ): String
}
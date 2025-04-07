package uz.xml.geminiapp.domain.repository

import android.graphics.Bitmap

interface GeminiRepository {
    suspend fun analyzeFood(bitmap: Bitmap): String
}
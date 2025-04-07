package uz.xml.geminiapp.di

import com.google.ai.client.generativeai.GenerativeModel
import uz.xml.geminiapp.presentation.analyze.AnalyzeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.xml.geminiapp.BuildConfig
import uz.xml.geminiapp.data.repository.GeminiRepositoryImpl
import uz.xml.geminiapp.domain.repository.GeminiRepository

val appModule = module {

    single {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )
    }

    single<GeminiRepository> { GeminiRepositoryImpl(get<GenerativeModel>()) }

    viewModel { AnalyzeViewModel(get<GeminiRepository>()) }
}
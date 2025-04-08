package uz.xml.geminiapp.di

import com.google.ai.client.generativeai.GenerativeModel
import org.koin.android.ext.koin.androidContext
import uz.xml.geminiapp.presentation.analyze.AnalyzeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.xml.geminiapp.BuildConfig
import uz.xml.geminiapp.data.repository.GeminiRepositoryImpl
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.presentation.camera.LanguageViewModel

val appModule = module {

    single {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )
    }

    single<GeminiRepository> {
        GeminiRepositoryImpl(
            generativeModel = get<GenerativeModel>(),
            context = androidContext()
        )
    }

    viewModel { AnalyzeViewModel(get<GeminiRepository>()) }

    viewModel { LanguageViewModel() }

}
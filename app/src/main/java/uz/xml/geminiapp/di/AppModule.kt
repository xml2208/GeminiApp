package uz.xml.geminiapp.di

import com.google.ai.client.generativeai.GenerativeModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.xml.geminiapp.BuildConfig
import uz.xml.geminiapp.data.repository.GeminiRepositoryImpl
import uz.xml.geminiapp.data.repository.ProfileRepositoryImpl
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.domain.repository.ProfileRepository
import uz.xml.geminiapp.presentation.analysis.AnalyzeViewModel
import uz.xml.geminiapp.presentation.camera.CameraViewModel
import uz.xml.geminiapp.presentation.language.LanguageViewModel
import uz.xml.geminiapp.presentation.profile.ProfileScreenViewModel
import uz.xml.geminiapp.presentation.daily_calorie.DailyCaloriesViewModel

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

    single<ProfileRepository> { ProfileRepositoryImpl(context = androidContext()) }

    viewModel { AnalyzeViewModel(get<GeminiRepository>()) }

    viewModel { LanguageViewModel() }

    viewModel { CameraViewModel() }

    viewModel { ProfileScreenViewModel(profileRepository = get<ProfileRepository>()) }

    viewModel { DailyCaloriesViewModel(geminiRepository = get<GeminiRepository>()) }

}
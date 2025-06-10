package uz.xml.geminiapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.ai.client.generativeai.GenerativeModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.xml.geminiapp.BuildConfig
import uz.xml.geminiapp.data.repository.GeminiRepositoryImpl
import uz.xml.geminiapp.data.repository.SettingsRepositoryImpl
import uz.xml.geminiapp.data.repository.UserPreferencesRepositoryImpl
import uz.xml.geminiapp.domain.repository.GeminiRepository
import uz.xml.geminiapp.domain.repository.SettingsRepository
import uz.xml.geminiapp.domain.repository.UserPreferencesRepository
import uz.xml.geminiapp.domain.usecase.GetSelectedLanguageUseCase
import uz.xml.geminiapp.presentation.analysis.AnalyzeViewModel
import uz.xml.geminiapp.presentation.camera.CameraViewModel
import uz.xml.geminiapp.presentation.daily_calorie.DailyCaloriesViewModel
import uz.xml.geminiapp.presentation.language.LanguageViewModel
import uz.xml.geminiapp.presentation.meal_plan.MealPlanViewModel
import uz.xml.geminiapp.presentation.profile.SettingsScreenViewModel

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

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

    single<SettingsRepository> { SettingsRepositoryImpl(context = androidContext()) }

    single { GetSelectedLanguageUseCase(settingsRepository = get<SettingsRepository>()) }

    single<DataStore<Preferences>> {
        get<Context>().userPreferencesDataStore
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(get())
    }

    viewModel { AnalyzeViewModel(get<GeminiRepository>()) }

    viewModel { LanguageViewModel() }

    viewModel { CameraViewModel(getSelectedLanguageUseCase = get<GetSelectedLanguageUseCase>()) }

    viewModel { SettingsScreenViewModel(settingsRepository = get<SettingsRepository>()) }

    viewModel {
        DailyCaloriesViewModel(
            geminiRepository = get<GeminiRepository>(),
            getSelectedLanguageUseCase = get<GetSelectedLanguageUseCase>(),
            dataStore = get<DataStore<Preferences>>()
        )
    }

    viewModel {
        MealPlanViewModel(
            geminiRepository = get<GeminiRepository>(),
            getSelectedLanguageUseCase = get<GetSelectedLanguageUseCase>(),
            userPreferencesRepository = get<UserPreferencesRepository>(),
        )
    }

}
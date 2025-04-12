package uz.xml.geminiapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import uz.xml.geminiapp.domain.repository.ProfileRepository
import uz.xml.geminiapp.presentation.language.LanguageManager
import uz.xml.geminiapp.presentation.navigation.CalorieApp

class MainActivity : ComponentActivity() {

    private val settingsRepository: ProfileRepository by inject()

    override fun attachBaseContext(newBase: Context) {
        val language = runBlocking { settingsRepository.selectedLanguage.first() }
        val contextWithLocale = LanguageManager.setAppLocale(newBase, language)
        super.attachBaseContext(contextWithLocale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieApp()
        }
    }
}
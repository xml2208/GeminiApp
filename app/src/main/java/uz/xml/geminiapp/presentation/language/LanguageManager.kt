package uz.xml.geminiapp.presentation.language

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    fun setAppLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun setLocale(context: Context, language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getCurrentLocale(context: Context): AppLanguage {
        val currentCode = context.resources.configuration.locales.get(0).language
        return AppLanguage.fromCode(currentCode)
    }

     fun Context.getLocalizedContext(language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.RUSSIAN -> Locale("ru")
            AppLanguage.UZBEK -> Locale("uz")
        }

        val config = Configuration(resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        return createConfigurationContext(config)
    }
}
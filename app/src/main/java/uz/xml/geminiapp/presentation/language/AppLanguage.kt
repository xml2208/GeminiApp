package uz.xml.geminiapp.presentation.language

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    RUSSIAN("ru"),
    UZBEK("uz");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ENGLISH
    }
}
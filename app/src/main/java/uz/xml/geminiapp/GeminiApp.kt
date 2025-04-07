package uz.xml.geminiapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uz.xml.geminiapp.di.appModule

class GeminiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GeminiApp)
            modules(appModule)
        }
    }
}
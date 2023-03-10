package su.salut.billingexample

import android.app.Application
import su.salut.billingexample.repository.SettingsRepository

class App : Application() {

    val settingsRepository: SettingsRepository by lazy(LazyThreadSafetyMode.NONE) {
        val config = object : SettingsRepository.Config {
            override val applicationId = BuildConfig.APPLICATION_ID
        }

        return@lazy SettingsRepository(config)
    }
}
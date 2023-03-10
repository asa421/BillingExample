package su.salut.billingexample.repository

class SettingsRepository(private val config: Config) {
    interface Config {
        val applicationId: String
    }

    fun getApplicationId(): String = config.applicationId
}
package su.salut.billingexample.repository

class SettingsRepository(private val config: Config) {
    interface Config {
        val productIds: List<String>
        val applicationId: String
    }

    fun getProductIds(): List<String> = config.productIds

    fun getApplicationId(): String = config.applicationId
}
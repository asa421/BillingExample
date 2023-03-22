package su.salut.billingexample.application.domain.repository

interface SettingsRepository {
    fun getProductIds(): List<String>
    fun getApplicationId(): String
}
package su.salut.billingexample.domain.repository

interface SettingsRepository {
    fun getProductIds(): List<String>
    fun getApplicationId(): String
}
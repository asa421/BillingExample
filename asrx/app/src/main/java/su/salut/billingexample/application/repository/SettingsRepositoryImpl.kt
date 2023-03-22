package su.salut.billingexample.application.repository

import su.salut.billingexample.application.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val productIds: List<String>,
    private val applicationId: String
) : SettingsRepository {
    override fun getProductIds(): List<String> = productIds

    override fun getApplicationId(): String = applicationId
}
package su.salut.billingexample.application.domain.usecase

import su.salut.billingexample.application.domain.repository.SettingsRepository

class GetProductIdsUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(): List<String> {
        return settingsRepository.getProductIds()
    }
}
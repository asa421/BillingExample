package su.salut.billingexample.domain.usecase

import su.salut.billingexample.domain.repository.SettingsRepository

class GetProductIdsUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(): List<String> {
        return settingsRepository.getProductIds()
    }
}
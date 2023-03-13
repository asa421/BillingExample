package su.salut.billingexample.domain.usecase

import su.salut.billingexample.domain.repository.SettingsRepository

class GetApplicationIdUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(): String {
        return settingsRepository.getApplicationId()
    }
}
package su.salut.billingexample.application.domain.usecase

import su.salut.billingexample.application.domain.repository.SettingsRepository

class GetApplicationIdUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(): String {
        return settingsRepository.getApplicationId()
    }
}
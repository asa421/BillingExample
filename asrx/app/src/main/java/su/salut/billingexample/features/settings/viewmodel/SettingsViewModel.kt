package su.salut.billingexample.features.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import su.salut.billingexample.application.App
import su.salut.billingexample.application.domain.usecase.GetApplicationIdUseCase
import su.salut.billingexample.application.domain.usecase.GetProductIdsUseCase

class SettingsViewModel(
    private val getProductIdsUseCase: GetProductIdsUseCase,
    private val getApplicationIdUseCase: GetApplicationIdUseCase
) : ViewModel() {

    private val _productIds = MutableLiveData<List<String>>().apply {
        value = getProductIdsUseCase.execute()
    }
    private val _applicationId = MutableLiveData<String>().apply {
        value = getApplicationIdUseCase.execute()
    }

    val productIds: LiveData<List<String>> = _productIds
    val applicationId: LiveData<String> = _applicationId

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

                return@initializer SettingsViewModel(
                    getProductIdsUseCase = app.factoryGetProductIdsUseCase(),
                    getApplicationIdUseCase = app.factoryGetApplicationIdUseCase()
                )
            }
        }
    }
}
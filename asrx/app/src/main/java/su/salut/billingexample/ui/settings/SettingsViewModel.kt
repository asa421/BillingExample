package su.salut.billingexample.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import su.salut.billingexample.App
import su.salut.billingexample.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _productIds = MutableLiveData<List<String>>().apply {
        value = settingsRepository.getProductIds()
    }
    private val _applicationId = MutableLiveData<String>().apply {
        value = settingsRepository.getApplicationId()
    }

    val productIds: LiveData<List<String>> = _productIds
    val applicationId: LiveData<String> = _applicationId

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = (this[APPLICATION_KEY] as App).settingsRepository

                return@initializer SettingsViewModel(settingsRepository = settingsRepository)
            }
        }
    }
}
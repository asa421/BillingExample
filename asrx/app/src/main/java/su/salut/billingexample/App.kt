package su.salut.billingexample

import android.app.Application
import android.content.Context
import android.util.Log
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import su.salut.billingexample.data.GoogleBillingStorage
import su.salut.billingexample.data.RuStoreBillingStorage
import su.salut.billingexample.data.extensions.OnLaunchBillingFlow
import su.salut.billingexample.domain.BillingInteractor
import su.salut.billingexample.domain.repository.BillingRepository
import su.salut.billingexample.domain.repository.SettingsRepository
import su.salut.billingexample.domain.usecase.GetApplicationIdUseCase
import su.salut.billingexample.domain.usecase.GetProductIdsUseCase
import su.salut.billingexample.lib.googleplay.GoogleBillingFactoryImpl
import su.salut.billingexample.lib.manager.BillingManager
import su.salut.billingexample.lib.manager.BillingManager.Companion.launchBillingFlow
import su.salut.billingexample.lib.rustore.RuStoreBillingFactoryImpl
import su.salut.billingexample.repository.BillingRepositoryImpl
import su.salut.billingexample.repository.SettingsRepositoryImpl
import su.salut.billingexample.repository.storage.BillingStorage

class App : Application() {

    private val settingsRepository: SettingsRepository by lazy(LazyThreadSafetyMode.NONE) {
        return@lazy SettingsRepositoryImpl(
            productIds = BuildConfig.PRODUCT_IDS.toList(),
            applicationId = BuildConfig.APPLICATION_ID
        )
    }

    private val billingRepository: BillingRepository by lazy(LazyThreadSafetyMode.NONE) {
        val storage = when (BillingManager.isRuStore) {
            true -> factoryRuStoreBillingStorage()
            false -> factoryGoogleBillingStorage(this@App)
        }

        return@lazy BillingRepositoryImpl(storage)
    }

    override fun onCreate() {
        super.onCreate()

        // Activate billing manager.
        BillingManager.activate(
            application = this,
            isRuStore = listOf("debugRuStore", "releaseRuStore").contains(BuildConfig.BUILD_TYPE),
            consoleApplicationId = BuildConfig.RUSTORE_APP_ID,
            deeplinkScheme = BuildConfig.DEEPLINK_RU_STORE
        )

        // RxJava2 UndeliverableException
        // https://proandroiddev.com/rxjava2-undeliverableexception-f01d19d18048
        // https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
        RxJavaPlugins.setErrorHandler { Log.e(TAG, it.message, it) }
    }

    fun factoryGetApplicationIdUseCase(): GetApplicationIdUseCase {
        return GetApplicationIdUseCase(settingsRepository)
    }

    fun factoryGetProductIdsUseCase(): GetProductIdsUseCase {
        return GetProductIdsUseCase(settingsRepository)
    }

    fun factoryBillingInteractor(): BillingInteractor {
        return BillingInteractor(billingRepository)
    }

    private fun factoryGoogleBillingStorage(context: Context): BillingStorage {
        val billingFactory = GoogleBillingFactoryImpl(context)
        val billingClientWrapper = su.salut.billingexample.lib
            .googleplay.BillingClientWrapperImpl(billingFactory)
        val onLaunchBillingFlow: OnLaunchBillingFlow = { billingClient, skuDetails ->
            billingClient.launchBillingFlow(skuDetails)
        }

        return GoogleBillingStorage(billingClientWrapper, onLaunchBillingFlow)
    }

    private fun factoryRuStoreBillingStorage(): BillingStorage {
        val billingFactory = RuStoreBillingFactoryImpl()
        val billingClientWrapper = su.salut.billingexample.lib
            .rustore.BillingClientWrapperImpl(billingFactory)

        return RuStoreBillingStorage(billingClientWrapper)
    }

    companion object {
        private const val TAG = "AR-AR"
    }
}
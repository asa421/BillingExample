package su.salut.billingexample.extensions.lib.manager

import android.app.Application
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.android.billingclient.api.SkuDetails
import io.reactivex.rxjava3.core.Completable
import su.salut.billingexample.application.domain.models.BillingException
import su.salut.billingexample.extensions.lib.googleplay.BillingClientWrapper
import su.salut.billingexample.extensions.lib.rustore.RuStoreBillingFactoryImpl
import kotlin.properties.Delegates

/**
 *
 * @date 02.02.2023
 * @author asa421
 */
class BillingManager {
    companion object {
        private val manager = ActivityManager()
        var isRuStore: Boolean by Delegates.notNull()
            private set

        @RequiresPermission("android.permission.INTERNET")
        fun activate(
            application: Application,
            isRuStore: Boolean,
            consoleApplicationId: String,
            deeplinkScheme: String
        ) {
            Companion.isRuStore = isRuStore

            when (isRuStore) {
                true -> RuStoreBillingFactoryImpl.init(
                    application = application,
                    consoleApplicationId = consoleApplicationId,
                    deeplinkScheme = deeplinkScheme
                )

                false -> manager.register(application)
            }
        }

        /**
         * For a successful return to the application, fore RuStore[RuStoreBillingFactoryImpl],
         * need to return the intent to the billing system!
         */
        fun onNewIntent(intent: Intent?) {
            if (isRuStore) { // Only for RuStore
                RuStoreBillingFactoryImpl.onNewIntent(intent)
            }
        }

        fun BillingClientWrapper.launchBillingFlow(skuDetails: SkuDetails): Completable {
            return when (val activity = manager.getActivity()) {
                null -> throw BillingException
                    .getErrorException("Error loading activity (BillingManager.launchBillingFlow).")
                else -> launchBillingFlowAsCompletable(activity, skuDetails)
            }
        }
    }
}
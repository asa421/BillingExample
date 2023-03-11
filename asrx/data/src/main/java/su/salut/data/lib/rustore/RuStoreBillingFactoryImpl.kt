package su.salut.data.lib.rustore

import android.app.Application
import android.content.Intent
import ru.rustore.sdk.billingclient.RuStoreBillingClient

class RuStoreBillingFactoryImpl: BillingClientWrapperImpl.GoogleBillingFactory {

    override fun buildBillingClient(): RuStoreBillingClient {
        return RuStoreBillingClient
    }

    companion object {
        fun init(application: Application, consoleApplicationId: String, deeplinkScheme: String) {
            RuStoreBillingClient.init(
                application = application,
                consoleApplicationId = consoleApplicationId,
                deeplinkScheme = deeplinkScheme
            )
        }

        fun onNewIntent(intent: Intent?) {
            RuStoreBillingClient.onNewIntent(intent)
        }
    }
}
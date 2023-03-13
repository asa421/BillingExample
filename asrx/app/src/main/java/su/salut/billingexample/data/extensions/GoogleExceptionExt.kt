package su.salut.billingexample.data.extensions

import com.android.billingclient.api.BillingClient.BillingResponseCode
import su.salut.billingexample.domain.models.BillingException
import su.salut.billingexample.domain.models.BillingResultCode
import su.salut.billingexample.lib.googleplay.BillingResultException

/** Convert response to exception. */
fun Throwable.mapGoogleToBillingException(): BillingException {
    return when (this) {
        is BillingException -> this

        is BillingResultException -> BillingException(
            responseCode = mapToBillingResultCode(),
            debugMessage = debugMessage,
            throwable = this
        )

        else -> BillingException(
            responseCode = BillingResultCode.ERROR,
            debugMessage = message ?: "",
            throwable = this
        )
    }
}

@BillingResultCode
private fun BillingResultException.mapToBillingResultCode(): Int {
    return when (responseCode) {
        BillingResponseCode.SERVICE_TIMEOUT -> BillingResultCode.SERVICE_TIMEOUT
        BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingResultCode.FEATURE_NOT_SUPPORTED
        BillingResponseCode.SERVICE_DISCONNECTED -> BillingResultCode.SERVICE_DISCONNECTED
        BillingResponseCode.OK -> BillingResultCode.OK
        BillingResponseCode.USER_CANCELED -> BillingResultCode.USER_CANCELED
        BillingResponseCode.SERVICE_UNAVAILABLE -> BillingResultCode.SERVICE_UNAVAILABLE
        BillingResponseCode.BILLING_UNAVAILABLE -> BillingResultCode.BILLING_UNAVAILABLE
        BillingResponseCode.ITEM_UNAVAILABLE -> BillingResultCode.ITEM_UNAVAILABLE
        BillingResponseCode.DEVELOPER_ERROR -> BillingResultCode.DEVELOPER_ERROR
        BillingResponseCode.ERROR -> BillingResultCode.ERROR
        BillingResponseCode.ITEM_ALREADY_OWNED -> BillingResultCode.ITEM_ALREADY_OWNED
        BillingResponseCode.ITEM_NOT_OWNED -> BillingResultCode.ITEM_NOT_OWNED

        else -> BillingResultCode.ERROR
    }
}

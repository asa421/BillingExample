package su.salut.billingexample.lib.googleplay

import com.android.billingclient.api.BillingResult

typealias IsOkResponse = (BillingResult) -> Boolean

/** Convert response to exception. */
fun BillingResult.mapToException(): BillingResultException {
    return BillingResultException(
        responseCode = responseCode,
        debugMessage = debugMessage,
        billingResult = this
    )
}
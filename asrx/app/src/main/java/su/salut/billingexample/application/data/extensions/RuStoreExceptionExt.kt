package su.salut.billingexample.application.data.extensions

import su.salut.billingexample.application.domain.models.BillingException
import su.salut.billingexample.application.domain.models.BillingResultCode
import su.salut.billingexample.extensions.lib.rustore.BillingResultException
import su.salut.billingexample.extensions.lib.rustore.ResponseCode
import ru.rustore.sdk.core.exception.*

/** Convert response to exception. */
fun Throwable.mapRuStoreToBillingException(): BillingException {
    return when (this) {
        is BillingException -> this

        is BillingResultException -> BillingException(
            responseCode = mapToBillingResultCode(),
            debugMessage = message ?: "",
            throwable = this
        )

        is RuStoreException -> mapToBillingException()

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
        ResponseCode.PAYMENT_RESULT_TIMEOUT,
        ResponseCode.TIMEOUT -> BillingResultCode.SERVICE_TIMEOUT

        ResponseCode.REQUEST_INVALID -> BillingResultCode.FEATURE_NOT_SUPPORTED

        ResponseCode.PAYMENT_RESULT_DECLINED_BY_SERVER,
        ResponseCode.APPLICATION_NOT_FOUND,
        ResponseCode.APPLICATION_INACTIVE -> BillingResultCode.SERVICE_DISCONNECTED

        ResponseCode.PAYMENT_RESULT_PURCHASE_RESULT,
        ResponseCode.PAYMENT_RESULT_INVOICE_RESULT,
        ResponseCode.OK -> BillingResultCode.OK


        ResponseCode.PAYMENT_RESULT_INVALID_INVOICE,
        ResponseCode.PAYMENT_RESULT_INVALID_PURCHASE,
        ResponseCode.PAYMENT_RESULT_CLOSED_BY_USER,
        ResponseCode.PAYMENT_RESULT_INVALID_PAYMENT_STATE -> BillingResultCode.USER_CANCELED

        ResponseCode.NOT_FOUND -> BillingResultCode.SERVICE_UNAVAILABLE

        ResponseCode.INVALID_TOKEN,
        ResponseCode.TOKEN_EXPIRED,
        ResponseCode.ACCESS_DENIED,
        ResponseCode.TOKEN_NOT_AUTHORIZED,
        ResponseCode.TOKEN_NOT_MATCH,
        ResponseCode.INVALID_TOKEN_TYPE -> BillingResultCode.BILLING_UNAVAILABLE

        ResponseCode.PRODUCT_NOT_FOUND,
        ResponseCode.PRODUCT_INACTIVE,
        ResponseCode.PRODUCT_REMOVED,
        ResponseCode.INVALID_CONSUME_PRODUCT -> BillingResultCode.ITEM_UNAVAILABLE

        ResponseCode.INVALID_PRODUCT_TYPE,
        ResponseCode.INVALID_QUANTITY -> BillingResultCode.DEVELOPER_ERROR

        ResponseCode.PURCHASE_EXISTS,
        ResponseCode.PRODUCT_INVOICE_CREATED,
        ResponseCode.PRODUCT_NON_CONSUMABLE_CONFIRMED,
        ResponseCode.PRODUCT_SUBSCRIPTION_CONFIRMED,
        ResponseCode.PRODUCT_CONSUMABLE-> BillingResultCode.ITEM_ALREADY_OWNED

        ResponseCode.PRODUCT_SUBSCRIPTION_GET_PRODUCTS,
        ResponseCode.ATTRIBUTES_DID_NOT_COME_IN_REQUEST,
        ResponseCode.FAILED_CHANGE_STATUS -> BillingResultCode.ITEM_NOT_OWNED

        else -> BillingResultCode.ERROR
    }
}

/** Convert response to exception. */
private fun RuStoreException.mapToBillingException(): BillingException {
    return when (this) {
        is RuStoreNotInstalledException -> BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "RuStore is not installed on the user's device!",
            throwable = this
        )

        is RuStoreOutdatedException ->  BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "RuStore installed on the user's device does not support payments!",
            throwable = this
        )

        is RuStoreUserUnauthorizedException ->  BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "The user is not authorized in RuStore!",
            throwable = this
        )

        is RuStoreApplicationBannedException ->  BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "The application is blocked in RuStore!",
            throwable = this
        )

        is RuStoreUserBannedException -> BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "The user is blocked in RuStore!",
            throwable = this
        )

        else -> BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = message ?: "",
            throwable = this
        )
    }
}

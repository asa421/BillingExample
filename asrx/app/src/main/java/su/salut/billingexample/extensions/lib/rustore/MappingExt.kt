package su.salut.billingexample.extensions.lib.rustore

import ru.rustore.sdk.billingclient.model.common.ResponseWithCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_CLOSED_BY_USER
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_DECLINED_BY_SERVER
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_INVALID_INVOICE
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_INVALID_PAYMENT_STATE
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_INVALID_PURCHASE
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_INVOICE_RESULT
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_PURCHASE_RESULT
import su.salut.billingexample.extensions.lib.rustore.ResponseCode.Companion.PAYMENT_RESULT_TIMEOUT

typealias IsOkResponse = (ResponseWithCode) -> Boolean
typealias IsOkPaymentResult = (PaymentResult) -> Boolean

/** Convert response to exception. */
fun ResponseWithCode.mapToException(): BillingResultException {
    return ResponseWithCodeException(
        responseCode = code,
        debugMessage = errorMessage ?: errorDescription,
        responseWithCode = this
    )
}

/** Convert result to exception. */
fun PaymentResult.mapToException(): BillingResultException {
    return PaymentResultException(
        responseCode = responseCode,
        debugMessage = debugMessage,
        purchaseId = purchaseId,
        finishCode = finishCode,
        paymentResult = this
    )
}

private val PaymentResult.responseCode: Int
    get() = when (this) {
        is PaymentResult.InvalidInvoice -> PAYMENT_RESULT_INVALID_INVOICE
        is PaymentResult.InvalidPaymentState -> PAYMENT_RESULT_INVALID_PAYMENT_STATE
        is PaymentResult.InvalidPurchase -> errorCode ?: PAYMENT_RESULT_INVALID_PURCHASE
        is PaymentResult.InvoiceResult -> PAYMENT_RESULT_INVOICE_RESULT
        is PaymentResult.PurchaseResult -> responseCode
    }


private val PaymentResult.PurchaseResult.responseCode: Int
    get() = when (finishCode) {
        PaymentFinishCode.SUCCESSFUL_PAYMENT -> PAYMENT_RESULT_PURCHASE_RESULT
        PaymentFinishCode.CLOSED_BY_USER -> PAYMENT_RESULT_CLOSED_BY_USER
        PaymentFinishCode.UNHANDLED_FORM_ERROR -> PAYMENT_RESULT_INVALID_PURCHASE
        PaymentFinishCode.PAYMENT_TIMEOUT -> PAYMENT_RESULT_TIMEOUT
        PaymentFinishCode.DECLINED_BY_SERVER -> PAYMENT_RESULT_DECLINED_BY_SERVER
        PaymentFinishCode.RESULT_UNKNOWN -> PAYMENT_RESULT_INVALID_PURCHASE
    }

private val PaymentResult.purchaseId: String?
    get() = when (this) {
        is PaymentResult.InvalidPurchase -> purchaseId
        is PaymentResult.PurchaseResult -> purchaseId
        else -> null
    }

private val PaymentResult.debugMessage: String
    get() = when (this) {
        is PaymentResult.InvalidInvoice -> "Invalid invoice."
        is PaymentResult.InvalidPaymentState -> "Invalid payment state."
        is PaymentResult.InvalidPurchase -> "Invalid purchase."
        is PaymentResult.InvoiceResult -> "Invoice result."
        is PaymentResult.PurchaseResult -> "Purchase result."
    }

private val PaymentResult.finishCode: PaymentFinishCode?
    get() = when (this) {
        is PaymentResult.InvoiceResult -> finishCode
        is PaymentResult.PurchaseResult -> finishCode
        else -> null
    }
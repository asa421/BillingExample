package su.salut.data.lib.rustore

import ru.rustore.sdk.billingclient.model.common.ResponseWithCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult

sealed class BillingResultException(debugMessage: String?): Exception(debugMessage) {
    abstract val responseCode: Int
}

data class ResponseWithCodeException(
    override val responseCode: Int,
    val debugMessage: String?,
    val responseWithCode: ResponseWithCode
) : BillingResultException(debugMessage)

data class PaymentResultException(
    override val responseCode: Int,
    val debugMessage: String?,
    val purchaseId: String?,
    val finishCode: PaymentFinishCode?,
    val paymentResult: PaymentResult
) : BillingResultException(debugMessage)
package su.salut.domain.models

import su.salut.domain.models.BillingResultCode.Companion.OK

/** Possible responses of the billing service layer. */
interface BillingResult {
    @BillingResultCode
    val responseCode: Int

    data class Ok(val debugMessage: String? = null) : BillingResult {
        override val responseCode: Int = OK
    }

    data class PurchaseResult(
        val purchases: List<Purchase>,
        val debugMessage: String? = null
    ) : BillingResult {
        override val responseCode: Int = OK
    }
}
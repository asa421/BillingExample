package su.salut.billingexample.lib.googleplay

import com.android.billingclient.api.BillingResult

data class BillingResultException(
    val responseCode: Int,
    val debugMessage: String,
    val billingResult: BillingResult? = null
) : Exception(debugMessage)
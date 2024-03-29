package su.salut.billingexample.extensions.lib.googleplay

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

data class PurchasesUpdated(
    val billingResult: BillingResult,
    val purchasesMade: List<Purchase>?
)
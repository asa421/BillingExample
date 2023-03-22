package su.salut.billingexample.application.data.models

import su.salut.billingexample.application.domain.models.Purchase

data class GooglePurchase(
    override val purchaseToken: String,
    override val productIds: List<String>,
    override val isPurchased: Boolean,
    override val isPending: Boolean,
    override val isTrialPeriod: Boolean,
    override val isAcknowledged: Boolean,
    val purchase: com.android.billingclient.api.Purchase
) : Purchase

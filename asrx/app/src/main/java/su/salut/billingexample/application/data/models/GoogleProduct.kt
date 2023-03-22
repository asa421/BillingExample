package su.salut.billingexample.application.data.models

import com.android.billingclient.api.SkuDetails
import su.salut.billingexample.application.domain.models.Product

data class GoogleProduct(
    override val productId: String,
    override val title: String,
    override val description: String,
    override val priceLabel: String,
    override val priceAmountMicros: Long,
    override val priceCurrencyCode: String,
    val skuDetails: SkuDetails
): Product
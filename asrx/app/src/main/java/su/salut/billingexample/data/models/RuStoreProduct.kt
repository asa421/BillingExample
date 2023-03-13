package su.salut.billingexample.data.models

import su.salut.billingexample.domain.models.Product

data class RuStoreProduct(
    override val productId: String,
    override val title: String,
    override val description: String,
    override val priceLabel: String,
    override val priceAmountMicros: Long,
    override val priceCurrencyCode: String,
    val product: ru.rustore.sdk.billingclient.model.product.Product
): Product
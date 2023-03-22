package su.salut.billingexample.application.data.models

import su.salut.billingexample.application.domain.models.Product

data class RuStoreProduct(
    override val productId: String,
    override val title: String,
    override val description: String,
    override val priceLabel: String,
    override val priceAmountMicros: Long,
    override val priceCurrencyCode: String,
    val product: ru.rustore.sdk.billingclient.model.product.Product
): Product
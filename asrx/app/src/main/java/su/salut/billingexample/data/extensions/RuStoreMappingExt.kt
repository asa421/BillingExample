package su.salut.billingexample.data.extensions

import su.salut.billingexample.data.models.RuStoreProduct
import su.salut.billingexample.data.models.RuStorePurchase
import su.salut.billingexample.domain.models.Product
import su.salut.billingexample.domain.models.Purchase
import java.util.*

private val ru.rustore.sdk.billingclient.model.purchase.Purchase.isPurchased: Boolean
    get() {
        return when (purchaseState) {
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.CONFIRMED,
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.CONSUMED,
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.PAID -> true
            else -> false
        }
    }

private val ru.rustore.sdk.billingclient.model.purchase.Purchase.isPending: Boolean
    get() {
        return when (purchaseState) {
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.INVOICE_CREATED,
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.CREATED -> true
            else -> false
        }
    }

private val ru.rustore.sdk.billingclient.model.purchase.Purchase.isAcknowledged: Boolean
    get() {
        return when (purchaseState) {
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.CONFIRMED,
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.CONSUMED -> true
            else -> false
        }
    }

private val ru.rustore.sdk.billingclient.model.purchase.Purchase.isCancelled: Boolean
    get() {
        return when (purchaseState) {
            ru.rustore.sdk.billingclient.model.purchase.PurchaseState.PAID -> false
            else -> true
        }
    }

fun List<ru.rustore.sdk.billingclient.model.purchase.Purchase>.toProductIds(): List<String> {
    return map { it.productId }.toSet().toList()
}

fun mapProductsToAppProducts(
    products: List<ru.rustore.sdk.billingclient.model.product.Product>
): List<Product> {
    return products.map(::mapProductToAppProduct)
}

/** @throws NoSuchElementException - if no such element is found. */
fun List<ru.rustore.sdk.billingclient.model.purchase.Purchase>.mapToAppPurchases(
    products: List<ru.rustore.sdk.billingclient.model.product.Product>
): List<Purchase> {
    return map(products::mapToAppPurchase)
}

fun ru.rustore.sdk.billingclient.model.product.Product.mapToAppPurchase(
    purchases: List<ru.rustore.sdk.billingclient.model.purchase.Purchase>
): List<Purchase> {
    return when (val purchase = purchases.firstOrNull { it.productId == productId }) {
        null -> emptyList()
        else -> listOf(mapToAppPurchase(purchase))
    }
}

private fun mapProductToAppProduct(
    product: ru.rustore.sdk.billingclient.model.product.Product
): Product {
    return RuStoreProduct(
        productId = product.productId,
        title = product.title ?: "",
        description = product.description ?: "",
        priceLabel = product.priceLabel ?: "",
        priceAmountMicros = product.price?.toLong()?.times(10_000) ?: 0,
        priceCurrencyCode = product.currency ?: "",
        product = product
    )
}

/** @throws NoSuchElementException - if no such element is found. */
private fun List<ru.rustore.sdk.billingclient.model.product.Product>.mapToAppPurchase(
    purchase: ru.rustore.sdk.billingclient.model.purchase.Purchase
): Purchase {
    val product = first { purchase.productId == it.productId }
    return product.mapToAppPurchase(purchase)
}

private fun ru.rustore.sdk.billingclient.model.product.Product.mapToAppPurchase(
    purchase: ru.rustore.sdk.billingclient.model.purchase.Purchase
): Purchase {
    val isTrialPeriod = checkIsTrialPeriod(purchase.purchaseTime)
    return purchase.mapToAppPurchase(isTrialPeriod)
}

private fun ru.rustore.sdk.billingclient.model.purchase.Purchase.mapToAppPurchase(
    isTrialPeriod: Boolean
): Purchase {
    return RuStorePurchase(
        purchaseToken = purchaseId ?: "",
        productIds = listOf(productId),
        isPurchased = isPurchased,
        isPending = isPending,
        isTrialPeriod = isTrialPeriod,
        isAcknowledged = isAcknowledged,
        isCancelled = isCancelled,
        purchase = this
    )
}

private fun ru.rustore.sdk.billingclient.model.product.Product.checkIsTrialPeriod(
    purchaseTime: Date?
): Boolean {
    purchaseTime ?: return false
    val freeTrialPeriod = subscription?.freeTrialPeriod ?: return false
    val currentTimeMillis = System.currentTimeMillis().takeIf { it > 0 } ?: 0 // NullPointer
    val calendar = Calendar.getInstance().apply { time = purchaseTime }
    // Set end free trial period data
    calendar.add(Calendar.DAY_OF_YEAR, freeTrialPeriod.days)
    calendar.add(Calendar.MONTH, freeTrialPeriod.months)
    calendar.add(Calendar.YEAR, freeTrialPeriod.years)

    return currentTimeMillis < calendar.timeInMillis
}

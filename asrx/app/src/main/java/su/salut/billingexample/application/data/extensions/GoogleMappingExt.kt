package su.salut.billingexample.application.data.extensions

import android.os.Build
import com.android.billingclient.api.SkuDetails
import io.reactivex.rxjava3.core.Completable
import su.salut.billingexample.application.data.models.GoogleProduct
import su.salut.billingexample.application.data.models.GooglePurchase
import su.salut.billingexample.application.domain.models.Product
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.lib.googleplay.BillingClientWrapper
import java.time.Duration
import java.time.format.DateTimeParseException

typealias OnLaunchBillingFlow = (BillingClientWrapper, SkuDetails) -> Completable

private val SkuDetails.freeTrialPeriodInSeconds: Long
    get() {
        return when {
            freeTrialPeriod.isBlank() -> 0

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> try {
                Duration.parse(freeTrialPeriod).seconds
            } catch (_: DateTimeParseException) {
                0
            }

            else -> try {
                @Suppress("DEPRECATION")
                su.salut.billingexample.lib.time.Duration.parse(freeTrialPeriod)
            } catch (_: RuntimeException) {
                0
            }
        }
    }

fun mapSkuDetailsToProducts(list: List<SkuDetails>): List<Product> {
    return list.map(::mapSkuDetailsToProduct)
}

fun List<com.android.billingclient.api.Purchase>.toSkusList(): List<String> {
    val skuList = mutableSetOf<String>()
    forEach { skuList.addAll(it.skus) }

    return skuList.toList()
}

fun List<com.android.billingclient.api.Purchase>.mapToAppPurchases(
    skuDetailsList: List<SkuDetails>
): List<Purchase> {
    return map(skuDetailsList::mapToAppPurchase)
}

private fun List<SkuDetails>.mapToAppPurchase(
    purchase: com.android.billingclient.api.Purchase
): Purchase {
    val skuDetailsList = filter { purchase.skus.contains(it.sku) }
    val isTrialPeriod = skuDetailsList.checkIsTrialPeriod(purchase.purchaseTime)
    return purchase.mapToAppPurchase(isTrialPeriod)
}

private fun com.android.billingclient.api.Purchase.mapToAppPurchase(
    isTrialPeriod: Boolean
): Purchase {
    return GooglePurchase(
        purchaseToken = purchaseToken,
        productIds = skus,
        isPurchased = purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED,
        isPending = purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PENDING,
        isTrialPeriod = isTrialPeriod,
        isAcknowledged = isAcknowledged,
        purchase = this
    )
}

private fun List<SkuDetails>.checkIsTrialPeriod(purchaseTime: Long): Boolean {
    val purchaseTimePeriodInSecond = (System.currentTimeMillis() - purchaseTime)
        .takeIf { it > 0 } ?: 0 // NullPointer
        .div(1000)

    return any { purchaseTimePeriodInSecond < it.freeTrialPeriodInSeconds }
}

private fun mapSkuDetailsToProduct(skuDetails: SkuDetails): Product {
    return GoogleProduct(
        productId = skuDetails.sku,
        title = skuDetails.title,
        description = skuDetails.description,
        priceLabel = skuDetails.price,
        priceAmountMicros = skuDetails.priceAmountMicros,
        priceCurrencyCode = skuDetails.priceCurrencyCode,
        skuDetails = skuDetails
    )
}
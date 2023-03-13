package su.salut.billingexample.lib.rustore.fortesting

import android.net.Uri
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.product.ProductStatus
import ru.rustore.sdk.billingclient.model.product.ProductSubscription
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import java.util.*

class EntitiesFactory {

    fun generateProduct(
        productId: String,
        productType: ProductType? = null,
        productStatus: ProductStatus = ProductStatus.ACTIVE,
        priceLabel: String? = null,
        price: Int? = null,
        currency: String? = null,
        language: String? = null,
        title: String? = null,
        description: String? = null,
        imageUrl: Uri? = null,
        promoImageUrl: Uri? = null,
        subscription: ProductSubscription? = null
    ): Product {
        return Product(
            productId = productId,
            productType = productType,
            productStatus = productStatus,
            priceLabel = priceLabel,
            price = price,
            currency = currency,
            language = language,
            title = title,
            description = description,
            imageUrl = imageUrl,
            promoImageUrl = promoImageUrl,
            subscription = subscription
        )
    }

    fun generatePurchase(
        purchaseId: String? = null,
        productId: String = "",
        productType: ProductType? = null,
        invoiceId: String? = null,
        description: String? = null,
        language: String? = null,
        purchaseTime: Date? = null,
        orderId: String? = null,
        amountLabel: String? = null,
        amount: Int? = null,
        currency: String? = null,
        quantity: Int? = null,
        purchaseState: PurchaseState? = null,
        developerPayload: String? = null,
        subscriptionToken: String? = null
    ): Purchase {
        return Purchase(
            purchaseId = purchaseId,
            productId = productId,
            productType = productType,
            invoiceId = invoiceId,
            description = description,
            language = language,
            purchaseTime = purchaseTime,
            orderId = orderId,
            amountLabel = amountLabel,
            amount = amount,
            currency = currency,
            quantity = quantity,
            purchaseState = purchaseState,
            developerPayload = developerPayload,
            subscriptionToken = subscriptionToken
        )
    }
}